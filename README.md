# 【Android】DataBinding库（MVVM设计模式）
** *本篇文章已授权微信公众号 guolin_blog （郭霖）独家发布 **
##什么是MVVM
说到DataBinding，就有必要先提起MVVM设计模式。
**Model–View–ViewModel**(**MVVM**) 是一个软件架构设计模式，相比MVVM，大家对MVC或MVP可能会更加熟悉。
- MVC：（VIew-Model-Controller）
早期将VIew、Model、Controller代码块进行划分，使得程序大部分分离，降低耦合。
- MVP：（VIew-Model-Presenter）由于MVC中View和Model之间的依赖太强，导致Activity中的代码过于臃肿。为了他们可以绝对独立的存在，慢慢演化出了MVP。在MVP中View并不直接使用Model，它们之间的通信是通过 Presenter (MVC中的Controller)来进行的。
- MVVM：（Model–View–ViewModel）
MVVM可以算是MVP的升级版，将 Presenter 改名为 ViewModel。关键在于View和Model的双向绑定，当View有用户输入后，ViewModel通知Model更新数据，同理Model数据更新后，ViewModel通知View更新。

##Data Binding
在Google I/O 2015上，伴随着Android M预览版发布的[Data Binding](https://developer.android.com/tools/data-binding/guide.html)兼容函数库。
不知道要扯什么了，还是直接上代码，来看看Data Binding的魅力吧。
- ####环境要求
Data Binding对使用的环境还是有一定要求的（这货有点挑）
*Android Studio版本在1.3以上*
*gradle的版本要在1.5.0-alpha1以上*
*需要在Android SDK manager中下载Android Support repository*
然后在对应的Module的build.gradle中添加
```java
android {
    ....
    dataBinding {
        enabled =true
    }
}
```
> Gradle需要升级版本的可以参考[升级Gradle版本](http://www.jianshu.com/p/00beddbe3dbc)

- ####创建对象
创建一个User类
```java
public class User {
    private String firstName;
    private String lastName;

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
```

- ####布局
在activity_main.xml中布局
```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android">
    <data>
        <import type="com.example.gavin.databindingtest.User"/>
        <variable
            name="user"
            type="User" />
    </data>
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        android:gravity="center"
        >
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@{user.firstName}"
            android:textSize="20sp" />
        <Button
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@{user.lastName}"
            android:textSize="25sp" />
    </LinearLayout>
</layout>
```
这里跟平时的布局有点不同，最外层是layout，里面分别是是data以及我们的布局。
**data**：声明了需要用到的user对象，type用于是定路径。
可以在TextView中的看到android:text="@{user.firstName}"， 这是什么鬼，没见过这么写的！！！
（不急，继续往下看）

- ####绑定数据
看看下面的MainActivity
```java
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        User user = new User("Micheal", "Jack");
        binding.setUser(user);
    }
}
```
问我ActivityMainBinding哪来的？我怎么知道...
ActivityMainBinding是根据布局文件的名字生成的，在后面加了Binding。
***运行下看看效果吧***
![效果](http://upload-images.jianshu.io/upload_images/1638147-0e962174d9fd8f74.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

有点懵逼了，就绑定了下而已，这些数据是怎么显示到界面上的。
![懵逼](http://upload-images.jianshu.io/upload_images/1638147-f6fd7af1c5de7930.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

> **他是怎么工作的？**
原来Data Binding 在程序代码正在编译的时候，找到所有它需要的信息。然后通过语法来解析这些表达式，最后生成一个类。
通过反编译我们可以看到，Data Binding为我们生成了databinding包，以及ActivityMainBinding类（[反编译可以参考这里](http://blog.csdn.net/vipzjyno1/article/details/21039349)）
![](http://upload-images.jianshu.io/upload_images/1638147-ffd1bcc515796270.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
看看我们在onCreate中最后调用的binding.setUser(user)，在ActivityMainBinding中可以看到这个方法。
![setUser方法](http://upload-images.jianshu.io/upload_images/1638147-f0366fe8eb4431f6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
我想就是这个 super.requestRebind()对数据进行了绑定，至于里面怎么实现的，有待进一步研究。

##更多用法
上面只是用一个简单的例子，展示了Data Binding的用法，如果想在实际项目中使用，可不是上面这例子可以搞定的。下面就来说说Data Bindig的更多用法。
- ####首先消除下大家对空指针的顾虑
自动生成的 **DataBinding** 代码会检查null，避免出现NullPointerException。
例如在表达式中@{user.phone}如果user == null 那么会为user.phone设置默认值null而不会导致程序崩溃（基本类型将赋予默认值如int为0，引用类型都会赋值null）
- ####自定义DataBinding名
如果不喜欢自动生成的Data Binding名，我们可以自己来定义
```xml
<data class="MainBinding">
    ....
</data>
```
class对应的就是生成的Data Binding名
- ####导包
跟Java中的用法相似，布局文件中支持import的使用，原来的代码是这样
```xml
<data>
     <variable name="user" type="com.example.gavin.databindingtest.User" />
 </data>
```
使用import后可以写成这样：
```xml
    <data>
        <import type="com.example.gavin.databindingtest.User"/>
        <variable
            name="user"
            type="User" />
    </data>
```
-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
遇到相同的类名的时候：
```xml
<data>
    <import type="com.example.gavin.databindingtest.User" alias="User"/>
    <import type="com.example.gavin.mc.User" alias="mcUser"/>
    <variable name="user" type="User"/>
    <variable name="mcUser" type="mcUser"/>
</data>
```
使用alias设置别名，这样user对应的就是com.example.gavin.databindingtest.User，mcUser就对应com.example.gavin.mc.User，然后
```xml
<TextView
  android:layout_width="wrap_content"
  android:layout_height="wrap_content"
  android:text="@{user.firstName}"/>
```
-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
当需要用到一些包时，在Java中可以自动导包，不过在布局文件中就没有这么方便了。需要使用import导入这些包，才能使用。如，需要用到View的时候
```xml
<data>
    <import type="android.view.View"/>
</data>
...
<TextView
...
android:visibility="@{user.isStudent ? View.VISIBLE : View.GONE}"
/>
```
**注意**：*只要是在Java中需要导入包的类，这边都需要导入，如：Map、ArrayList等，不过java.lang包里的类是可以不用导包的*

- ####表达式
在布局中，不仅可以使用
```xml
android:text="@{user.lastName}"
```
还可以使用表达式如：
#####三元运算
在User中添加boolean类型的isStudent属性，用来判断是否为学生。
```xml
<TextView
android:layout_width="wrap_content"
android:layout_height="wrap_content"
android:text='@{user.isStudent? "Student": "Other"}'
android:textSize="30sp"/>
```
**注意**：*需要用到双引号的时候，外层的双引号改成单引号。*
还可以这样用
```xml
<TextView
android:layout_width="wrap_content"
android:layout_height="wrap_content"
android:text="学生"
android:visibility="@{user.isStudent ? View.VISIBLE : View.GONE}"
android:textSize="30sp"/>
```
这里用到的View需要在data中声明
```xml
<data>
     <import type="android.view.View"/>
</data>
```
**注意**：android:visibility="@{user.isStudent ? View.VISIBLE : View.GONE}",*可能会被标记成红色，不用管它编译会通过的*
#####？？
除了常用的操作法，另外还提供了一个 null 的合并运算符号 ??，这是一个三目运算符的简便写法。
```java
contact.lastName ?? contact.name
```
相当于
```java
contact.lastName != null ? contact.lastName : contact.name
```
>**所支持的操作符如下：**
 数学运算符 + - / * %
字符串拼接 +
逻辑运算 && ||
二进制运算 & | ^
一元运算符 + - ! ~
位运算符 >> >>> <<
比较运算符 == > < >= <=
instanceof
Grouping ()
文字 - character, String, numeric, null
类型转换 cast
方法调用 methods call
字段使用 field access
数组使用 [] Arrary access
三元运算符 ? :

- #####显示图片
除了文字的设置，网络图片的显示也是我们常用的。来看看Data Binding是怎么实现图片的加载的。
首先要提到BindingAdapter注解，这里创建了一个类，里面有显示图片的方法。
```java
public class ImageUtil {
    /**
     * 使用ImageLoader显示图片
     * @param imageView
     * @param url
     */
    @BindingAdapter({"bind:image"})
    public static void imageLoader(ImageView imageView, String url) {
        ImageLoader.getInstance().displayImage(url, imageView);
    }
}
```
*（这方法必须是public static的，否则会报错）*
这里只用了bind声明了一个image自定义属性，等下在布局中会用到。
这个类中只有一个静态方法imageLoader，里面有两参数，一个是需要设置图片的view，另一个是对应的Url，这里使用了ImageLoader库加载图片。
看看吧它的布局是什么样的吧
```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <data >
        <variable
            name="imageUrl"
            type="String"/>
    </data>

    <LinearLayout

        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        android:gravity="center"
        >
        <ImageView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:image = "@{imageUrl}"/>
    </LinearLayout>
</layout>
```
最后在MainActivity中绑定下数据就可以了
```java
binding.setImageUrl(
    "http://115.159.198.162:3000/posts/57355a92d9ca741017a28375/1467250338739.jpg");
```
*哇靠！！！就这样？我都没看出来它是怎么设置这些图片的。*
不管了，先看看效果。（其中的原理以后慢慢唠，这里就负责说明怎么使用，这篇已经够长了，不想再写了）
![看个美女压压惊](http://upload-images.jianshu.io/upload_images/1638147-42b66d69c5af3ecc.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
>使用BindingAdapter的时候，我这还出现了这样的提示，不过不影响运行。不知道你们会不会...
![](http://upload-images.jianshu.io/upload_images/1638147-97e2b40db38de27d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
**【已解决】**
感谢[颜路](http://www.jianshu.com/users/296d6ebc6cd2)同学指出*@BindingAdapter({"bind:image"}) 改成 @BindingAdapter({"image"}) 就不会有警告了*

- #####点击事件
在MainActivity中声明方法：
```java
//参数View必须有，必须是public，参数View不能改成对应的控件，只能是View，否则编译不通过
public void onClick(View view) {
    Toast.makeText(this,"点击事件", Toast.LENGTH_LONG).show();
}
```
布局中：
```xml
    <data>
      ...
        <variable
        name="mainActivity"
        type="com.example.gavin.databindingtest.MainActivity"/>
    </data>
    ....
        <Button
            ...
            android:onClick="@{mainActivity.onClick}"
            />
```
最后记得在MainActivity中调用
```java
binding.setMainActivity(this);
```
*（发现：布局文件中，variable中的name，在binding中都会生成一个对应的set方法，如：setMainActivity。有set方法，那就应该有get方法，试试getMainActivity，还真有）*
**运行下看看效果**
![点击事件](http://upload-images.jianshu.io/upload_images/1638147-64b01e08adae35d9.gif?imageMogr2/auto-orient/strip)
当然如果你不想吧点击事件写在MainActivity中，你把它单独写在一个类里面：
```java
public class MyHandler {
    public void onClick(View view) {
        Toast.makeText(view.getContext(), "点击事件", Toast.LENGTH_LONG).show();
    }
}
```
```xml
    <data>
      ...
        <variable
        name="handle"
        type="com.example.gavin.databindingtest.MyHandler"/>
    </data>
    ....
        <Button
            ...
            android:onClick="@{handle.onClick}"
            />
    </data>
```
在MainActivity调用
```java
binding.setHandle(new MyHandler());
```
- ####调用Activity中的变量
上面看到它调用MainActivity中的onClick方法，那么可以调用MainActivity中的属性吗？
在MainActivity中定义mName，
```java
public static String mName = "MM";
```
布局中
```xml
    <data>
        ...
        <variable
            name="mainActivity"
            type="com.example.gavin.databindingtest.MainActivity"/>
    </data>
        <Button
            ...
            android:text="@{mainActivity.mName}"
            />
```
**注意**：*这个变量必须是public static*
- ####数据改变时更新UI
当数据发生变化时，我们可以这样更新UI
```java
    private ActivityMainBinding binding;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        user = new User("Micheal", "Jack");
        binding.setUser(user);
        binding.setHandle(new MyHandler());
        delay();
    }
    /**
     * 两秒后改变firstName
     */
    private void delay() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                user.setFirstName("Com");
                binding.setUser(user);
            }
        }, 2000);
    }
```
看看调用的这个setUser是什么：
![setUser](http://upload-images.jianshu.io/upload_images/1638147-ff43d8156f8baa78.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
从反编译的代码中可以看出，setUser方法中重新绑定了数据。
看下效果

![效果](http://upload-images.jianshu.io/upload_images/1638147-f85afcbd74ac53d4.gif?imageMogr2/auto-orient/strip)

- ####**BaseObservable**
使用上面的代码实现了UI的更新你就满足了？其实官方为我们提供了更加简便的方式，使User继承BaseObservable，代码如下
```java
public class User extends BaseObservable {
    private String firstName;
    private String lastName;

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
    @Bindable
    public String getFirstName() {
        return this.firstName;
    }
    @Bindable
    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        notifyPropertyChanged(BR.lastName);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        notifyPropertyChanged(BR.firstName);
    }
}
```
只要user发生变化，就能达到改变UI的效果。在MainActivity中只要调用以下代码
```java
user.setFirstName("Com");
```
有了BaseObservable就够了？不不不，我比较懒，不想写那么多@Bindable和notifyPropertyChanged。万一里面有几十个属性，那不写哭起来？而且还有可能写丢了。
Data Binding的开发者贴心得为我们准备了一系列的[ObservableField](https://developer.android.com/reference/android/databinding/ObservableField.html)，包括： [ObservableBoolean](https://developer.android.com/reference/android/databinding/ObservableBoolean.html), [ObservableByte](https://developer.android.com/reference/android/databinding/ObservableByte.html), [ObservableChar](https://developer.android.com/reference/android/databinding/ObservableChar.html), [ObservableShort](https://developer.android.com/reference/android/databinding/ObservableShort.html), [ObservableInt](https://developer.android.com/reference/android/databinding/ObservableInt.html), [ObservableLong](https://developer.android.com/reference/android/databinding/ObservableLong.html), [ObservableFloat](https://developer.android.com/reference/android/databinding/ObservableFloat.html),[ObservableDouble](https://developer.android.com/reference/android/databinding/ObservableDouble.html), 以及 [ObservableParcelable](https://developer.android.com/reference/android/databinding/ObservableParcelable.html)看看它们的用法
**ObservableField的使用**
1、创建User2
```java
public class User2 {
    public final ObservableField<String> firstName = new ObservableField<>();
    public final ObservableField<String> lastName = new ObservableField<>();
    public final ObservableInt age = new ObservableInt();
    public final ObservableBoolean isStudent = new ObservableBoolean();
}
```
这类里面没有Get/Set。
2、布局文件
```xml
        <TextView
            ...
            android:text="@{user2.firstName}" />
        <TextView
            ...
            android:text="@{user2.lastName}" />
        <TextView
            ...
            android:text="@{String.valueOf(user2.age)}"
             />
```
3、MainActivity中
```java
        mUser2 = new User2();
        binding.setUser2(mUser2);
        mUser2.firstName.set("Mr");
        mUser2.lastName.set("Bean");
        mUser2.age.set(20);
        mUser2.isStudent.set(false);
```
这里new了一个User2对象后，直接就绑定了。之后只要mUser2中的数据发生变化，UI也会随之更新。
除了这几个Map跟List也是必不可少的，Data Binding为我们提供了 [ObservableArrayMap](https://developer.android.com/reference/android/databinding/ObservableArrayMap.html)和[ObservableArrayList](https://developer.android.com/reference/android/databinding/ObservableArrayList.html)。
**ObservableArrayMap的使用**
```java
ObservableArrayMap<String, Object> user = new ObservableArrayMap<>();
user.put("firstName", "Google");
user.put("lastName", "Inc.");
user.put("age", 17);
```
```java
<data>
    <import type="android.databinding.ObservableMap"/>
    <variable name="user" type="ObservableMap<String, Object>"/>
</data>
…
<TextView
   android:text='@{user["lastName"]}'
   android:layout_width="wrap_content"
   android:layout_height="wrap_content"/>
<TextView
   android:text='@{String.valueOf(1 + (Integer)user["age"])}'
   android:layout_width="wrap_content"
   android:layout_height="wrap_content"/>
```
**ObservableArrayList的使用**
```java
ObservableArrayList<Object> user = new ObservableArrayList<>();
user.add("Google");
user.add("Inc.");
user.add(17);
```
```xml
<data>
    <import type="android.databinding.ObservableList"/>
    <import type="com.example.my.app.Fields"/>
    <variable name="user" type="ObservableList<Object>"/>
</data>
…
<TextView
   android:text='@{user[Fields.LAST_NAME]}'
   android:layout_width="wrap_content"
   android:layout_height="wrap_content"/>
<TextView
   android:text='@{String.valueOf(1 + (Integer)user[Fields.AGE])}'
   android:layout_width="wrap_content"
   android:layout_height="wrap_content"/>
```
> 在布局中使用到ObservableBoolean 类型时，编译无法通过
```java
android:text='@{user2.isStudent?"学生":"非学生"}'
```
**【目前已知】**
将中文改成英文是可以通过编译的，像下面这样
```java
android:text='@{user2.isStudent?"Student":"Not Student"}'
```
为何使用中文不可以？原因未明。（感谢指教）

- ####在RecyclerView或ListView中使用
前面说了那么多基础的用法，可还是不能达到我们的需求。几乎在每个app中都有列表的存在，RecyclerView或ListView，从上面所说的似乎还看不出Data Binding在RecyclerView或ListView中是否也能起作用。（用屁股想也知道，Google的开发团对怎么可能会犯这么低级的错误）。下面以RecyclerView为例子：
1、直接看Item的布局（user_item.xml）：
```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android">
    <data>
        <variable
            name="user2"
            type="com.example.gavin.databindingtest.User2" />
    </data>
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:padding="10dp"
        android:orientation="horizontal">
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@{user2.firstName}"/>
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="·"/>
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@{user2.lastName}"/>
        <View
            android:layout_width="0dp"
            android:layout_height="0dp"
            android:layout_weight="1"/>
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text='@{user2.age+""}'/>
    </LinearLayout>
</layout>
```
2、RecyclerView的数据绑定是在Adapter中完成的，下面看看Adapter（这里使用了一个Adapter，如果你在使用的时候发现RecyclerView的动画没了，去[这里](https://realm.io/cn/news/data-binding-android-boyar-mount/)寻找答案）
```java
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {

    private List<User2> mData = new ArrayList<>();

    public MyAdapter(List<User2> data) {
        this.mData = data;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return MyHolder.create(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.bindTo(mData.get(position));
    }

    @Override
    public int getItemCount() {
        if (mData == null)
            return 0;
        return mData.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {
        private UserItemBinding mBinding;

        static MyHolder create(LayoutInflater inflater, ViewGroup parent) {
            UserItemBinding binding = UserItemBinding.inflate(inflater, parent, false);
            return new MyHolder(binding);
        }

        private MyHolder(UserItemBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        public void bindTo(User2 user) {
            mBinding.setUser2(user);
            mBinding.executePendingBindings();
        }

    }
}
```
3、最后在布局和MainActivity中的使用跟平时的用法一样
布局中加入RecyclerView：
```xml
        <android.support.v7.widget.RecyclerView
            android:id="@+id/recycler_view"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>
```
MainActivity中：
```java
        List<User2> data = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            User2 user2 = new User2();
            user2.age.set(30);
            user2.firstName.set("Micheal " + i);
            user2.lastName.set("Jack " + i);
            data.add(user2);
        }
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new MyAdapter(data));
```
这样就可以了。
不过，在自动生成的ActivityMainBinding中，我们可以看到根据RecyclerView的id，会自动生成一个recyclerView。
![](http://upload-images.jianshu.io/upload_images/1638147-25cf2c6224318135.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
所以在MainActivity中，我们可以不用findViewById，直接使用binding.recyclerView。
```java
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(new MyAdapter(data));
```
来看看效果吧：
![RecyclerView](http://upload-images.jianshu.io/upload_images/1638147-ca6f78541abc5d99.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


##Tips：
- #####tip1：若需要显示int类型，需要加上""：如
user.age为int类型，需要这样用
```xml
<TextView   
  android:layout_width="wrap_content"
  android:layout_height="wrap_content"
  android:text='@{""+user.age}'/>
```
或者
```xml
<TextView   
  android:layout_width="wrap_content"
  android:layout_height="wrap_content"
  android:text="@{String.valueOf(user.age)}"/>
```

- #####tip2：不建议新手使用，出现错误的时候根据提示，不容易找到出错位置。（是根本找不到...）

##参考
[Google官方](https://developer.android.com/topic/libraries/data-binding/index.html#data_binding_layout_files)（权威，不过全英文。点击事件写的好像不对，后来去其他地方查的）：
[Realm](https://realm.io/cn/news/data-binding-android-boyar-mount/)（十分全面）：
[CSDN-亓斌](http://blog.csdn.net/qibin0506/article/details/47393725)（有点像google文档的翻译版，整体结果相似）：
[阳春面的博客](https://www.aswifter.com/2015/07/04/android-data-binding-1/)（好奇怪的名字）

源码地址http://download.csdn.net/detail/z437955114/9609513

> 以上有错误之处感谢指出
