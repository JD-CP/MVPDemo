# Android架构设计之一：MVP

对于新入门或者以及有过一段开发经验对 MVP 仍有困惑的 Android 开发者，这篇文章，希望你不要错过。

本文主要讲解了最基础 MVP ，从 0 到 1 的实现过程，以及如何解决实现过程中遇到的问题。

####简介

关于 Android 架构，目前主要有 MVC、MVP、MVVM、模块化、组件化等。

 - **MVC** ： Model - View - Controller
 
 M：逻辑模型，V：视图模型，C：控制器

 但这种架构，相较于其他几种，比较落后。 而且耦合性严重，

 职责相对不够明确不利于后期维护，

 适用于小型的一次性项目。

 - **MVP** ： Model - View - Presenter

 Model：包含具体的数据源以及数据请求

 View：负责 UI 处理，对应 Activity、Fragment

 Presenter：负责收取 View 发起的操作指令，并根据指令调用 Model 方法，

 获取数据，  并对获取的数据进行整合，再回调给 View。

 目前 MVP 是应用比较广泛的一种架构，

 层次清晰，耦合度降低，同时 View 只负责 UI 即可，释放了 View。

 但是，在加入 Presenter 作为 View 和 Model 的桥梁的同时，

 也导致了 Presenter 会越来越臃肿，也不利于后期的维护。

 并且，每一个包含网络请求的 View 都需要对应一个或多个 Presenter。

 - **MVVM**：Model - View - ViewModel

 相对来说，MVVM 实际上是 MVP 的改进版，

 将 Presenter 改为 ViewModel，并配合 Databinding，

 通过双向数据绑定来实现视图与数据的交互。

 MVVM 目前相较 MVP，应用较少，调试不够方便，

 架构的实现方式不够完善，常见的只有 Databinding 框架，

 中小型项目不适用用这种架构。但它简化了开发，数据和视图只需要绑定一次即可。

 -  **模块化**：独立、解耦、可重性

 对一系列具有内聚性的业务进行整理，将其与其他业务进行切割、拆分，

 从主工程或者原位置抽离为一个相对独立的部分。

 不同的模块之间，相互独立，不存在依赖与被依赖的关系。大大减少了耦合度。

 既可以以 Library 的形式供主工程依赖，又可以以 Application 的形式，

 脱离主工程独立运行，独立调试。这样就i使得在以后的版本维护及迭代中，

 各个业务线的开发人员的职责更加明确。

 各个模块之间还可以组合运行，能够及时适应产品的需求，灵活拆分组合打包上线。

 目前应用较多的框架主要有：

 阿里的 [ARouter](https://github.com/alibaba/ARouter)、

 得到开源的 [DDComponentForAndroid](https://github.com/luojilab/DDComponentForAndroid)

 - **组件化**：

 将通用的一个功能或 UI 库做成一个组件。

 比如及时通讯、支付、分享、推送、下拉刷新等。

 模块化是根据业务抽离，组件化是根据功能 UI 抽离。

 一个模块可以依赖多个组件，组件与组件之间不可相互依赖。

####MVP 具体实现

假设现在有这样一个需求，在某一个页面，当用户点击按钮，从网络获取数据并展示在当前页。

这是一个很简单的需求，让我们拆分一下，整理一下实现思路：

 - **View**：对应某一个页面，按钮的点击操作，属于和用户的交互
 - **Model**：对应从网络获取数据
 - **Presenter**：负责从 **Model** 获取数据，并回调给 **View**，**View** 拿到数据后进行展示

ok，思路有了，现在用代码进行实现：

 1. 创建 Model 类，封装通过网络请求获取数据的过程，即 M 层
	 
```
/**
 * model 层：从数据源（网络、数据库）获取数据
 */
public class DataModel {

    private DataApi mApi;

    public DataModel() {
        mApi = RetrofitHelpter.createApi(DataApi.class);
    }

    public void getData(String appKey, Callback<BaseResponse> callback) {
        Call<BaseResponse> responseCall = mApi.getData(appKey);
        // 发起请求
        responseCall.enqueue(callback);
    }

}
```

 2. 在创建 Presenter 层之前，我们需要创建一个接口，取名 DataView，负责向 V 层回调数据
 
```
public interface DataView {

    void getDataSuccess(List<ArticleBean> articleList);

    void getDataFail(String failMsg);

}
```

 3. 现在创建 Presenter，取名 DataPresenter
 

```
/**
 * 负责 View 层和 Model 层之间的通信，并对从 Model 层获取的数据进行处理
 */
public class DataPresenter {

    private DataView mView;
    private DataModel mModel;

    public DataPresenter(DataView dataView) {
        this.mView = dataView;
        this.mModel = new DataModel();
    }

    /**
     * 定义 View 层需要进行的 action
     */
    public void getData(String appKey) {
        mModel.getData(appKey, new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                mView.getDataSuccess(response.body().getResult().getList());
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                mView.getDataFail(t.getMessage());
            }
        });
    }

}
```

 4.  在我们的 View 层，实现 DataView 接口，并重写方法，这样就能接收到回调数据了
 

```
/**
 * View 层，负责 UI 绘制以及与用户的交互
 */
public class MVPDemoAty extends AppCompatActivity implements DataView {

    private static final String APP_KEY = "dbb6893ab0913b02724696504181fe39";

    private Button btnGet;
    private RecyclerView recyclerView;

    private DataPresenter mPresenter;

    private DataAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvp_demo);

        btnGet = findViewById(R.id.btnGet);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mPresenter = new DataPresenter(this);

        mAdapter = new DataAdapter(this, new ArrayList<ArticleBean>());
        recyclerView.setAdapter(mAdapter);

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getData(APP_KEY);
            }
        });

    }

    @Override
    public void getDataSuccess(List<ArticleBean> articleList) {
        mAdapter.setNewData(articleList);
    }

    @Override
    public void getDataFail(String failMsg) {
        Toast.makeText(this, failMsg, Toast.LENGTH_SHORT).show();
    }
}
```
ok，敲完上面的代码，运行项目，点击获取，就会看到下面的界面：

   ![屏幕截图](https://img-blog.csdn.net/20180816150729792?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzMzNjIyNjk5/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

说明我们已经完成了最简单 MVP 的编写。

细心的同学可能会发现，我们在 DataPresenter 中持有了 V 层的引用。

这个问题就很严重了，如果在获取网络数据的时候，当前的 Activity 就被销毁了，那么就会引起内存泄漏。

如何避免呢？解决方法也很简单，只需要在 Activity 销毁时，将 V 层的引用置空不就可以了？

ok，思路有了，往下看代码实现：

 

 - 编写 IPresenter 接口，提供绑定和解绑两个方法

```
public interface IPresenter {

    void attach(DataView dataView);

    void detach();

}
```


 - DataPresenter 类实现 IPresenter

```
/**
 * 负责 View 层和 Model 层之间的通信，并对从 Model 层获取的数据进行处理
 */
public class DataPresenter implements IPresenter {

    private DataView mView;
    private DataModel mModel;

    public DataPresenter() {
        this.mModel = new DataModel();
    }

    /**
     * 定义 View 层需要进行的 action
     */
    public void getData(String appKey) {
        mModel.getData(appKey, new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                mView.getDataSuccess(response.body().getResult().getList());
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                mView.getDataFail(t.getMessage());
            }
        });
    }

    @Override
    public void attach(DataView dataView) {
        this.mView = dataView;
    }

    @Override
    public void detach() {
        this.mView = null;
    }
}
```

 

 - 在 V 层中，创建 Presenter 完成之后，就绑定`mPresenter.attach(this);`，
 
   并在销毁时，调用 `mPresenter.detach();` 修改后的代码如下：


```
/**
 * View 层，负责 UI 绘制以及与用户的交互
 */
public class MVPDemoAty extends AppCompatActivity implements DataView {

    private static final String APP_KEY = "dbb6893ab0913b02724696504181fe39";

    private Button btnGet;
    private RecyclerView recyclerView;

    private DataPresenter mPresenter;

    private DataAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvp_demo);

        btnGet = findViewById(R.id.btnGet);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mPresenter = new DataPresenter();
        mPresenter.attach(this);

        mAdapter = new DataAdapter(this, new ArrayList<ArticleBean>());
        recyclerView.setAdapter(mAdapter);

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getData(APP_KEY);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detach();
    }

    @Override
    public void getDataSuccess(List<ArticleBean> articleList) {
        mAdapter.setNewData(articleList);
    }

    @Override
    public void getDataFail(String failMsg) {
        Toast.makeText(this, failMsg, Toast.LENGTH_SHORT).show();
    }
}
```

写到这里，对于内存泄漏问题，我们已经完美的解决了。但对于整个 MVP 的实现，貌似还不是那么完美。

有什么问题呢？在上面的代码中，我们在 V 层实现了 Presenter 的绑定与解绑操作。但是，在实际应用开发

过程中，会有很多个涉及网络请求操作的 Activity，难不成每个 Activity 都要去实现重写绑定与解绑？

很明显，这样做是可以的！哈哈哈！但是出于对自己的严格要求以及对代码质量的不断追求，

显然，优化工作是一定要做的！那么，下面，我们的任务就是如何优化、如何扫除多余臃肿代码？

 

 - **创建一个抽象基类**：BaseMVPActivity，提供 createPresenter()，并返回 Presenter 对象，

    这样拿到了子类的 Presenter 对象，就可以进行绑定解绑操作了。
      
```
public abstract class BaseMVPActivity<T extends IPresenter> extends AppCompatActivity implements IView {

    protected T mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(createView());

        mPresenter = createPresenter();
        if (null == mPresenter) {
            throw new IllegalStateException("Please call mPresenter in BaseMVPActivity(createPresenter) to create!");
        } else {
            mPresenter.attach(this);
        }

        viewCreated();

    }

    protected abstract void viewCreated();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPresenter){
            mPresenter.detach();
        }
    }

    protected abstract int createView();
    protected abstract T createPresenter();

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

}
```

 - **创建基类**：BasePresenter，并实现 IPresenter，重写绑定解绑方法。
 
```
public class BasePresenter<V extends IView> implements IPresenter<V> {

    protected V view;

    @Override
    public void attach(V view) {
        this.view = view;
    }

    @Override
    public void detach() {
        this.view = null;
    }

}
```

 - **创建接口**：IView，可以是空实现，也可以声明一些共用的方法。
 
```
public interface IView {

    void showLoading();

    void hideLoading();

}
```
修改之后的 Activity：

```
/**
 * View 层，负责 UI 绘制以及与用户的交互
 */
public class MVPDemoAty extends BaseMVPActivity<DataPresenter> implements DataView {

    private static final String APP_KEY = "dbb6893ab0913b02724696504181fe39";

    private Button btnGet;
    private RecyclerView recyclerView;

    private DataAdapter mAdapter;

    private ProgressDialog mDialog;

    @Override
    protected void viewCreated() {
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("玩命加载中...");

        btnGet = findViewById(R.id.btnGet);
        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        // linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        mAdapter = new DataAdapter(this, new ArrayList<ArticleBean>());
        recyclerView.setAdapter(mAdapter);

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.show();
                mPresenter.getData(APP_KEY);
            }
        });
    }

    @Override
    protected int createView() {
        return R.layout.activity_mvp_demo;
    }

    @Override
    protected DataPresenter createPresenter() {
        return new DataPresenter();
    }

    @Override
    public void getDataSuccess(List<ArticleBean> articleList) {
        mDialog.dismiss();
        mAdapter.setNewData(articleList);
    }

    @Override
    public void getDataFail(String failMsg) {
        mDialog.dismiss();
        Toast.makeText(this, failMsg, Toast.LENGTH_SHORT).show();
    }

}
```
到此，一个较完善的 MVP 架构，已经实现的差不多了。上面实现的是 Activity 的MVP实现，

Fragment 也是一样，在这里就不实现了。当然，对于上面实现的 MVP 仍然还有很多可以优化之处，

时间有限，就先实现到这里，以后有时间再改造。最后，附上 Github 下载地址：

[MVPDemo](https://github.com/JD-CP/MVPDemo)