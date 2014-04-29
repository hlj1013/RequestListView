RequestListView
============

* RequestListView是基于ListView的控件。
* 通过少量参数达到分页效果。
* 请求源于网络的Json数据。
* 支持请求完成、请求错误的回调。

```java
private AjaxListView mListView;

mListView = (RequestListView) findViewById(R.id.requestlv);
mListView.setAdapter(mAdapter);
mListView.showResult("http://gitdemo.duapp.com/RequestData");
mListView.setOnCompleteListener(new OnCompleteListener() {
	@Override		
	public void onSuccess(String str) {
		mList.addAll(JSON.parseArray(str, ArrayBean.class));
		mAdapter.notifyDataSetChanged();
	}
	@Override
	public void onFail(String str) {

	}
});
```
## 效果图

![image01](http://github.com/haoyuexing/RequestListView/raw/master/ScreenSnap/image01.png)
![image02](http://github.com/haoyuexing/RequestListView/raw/master/ScreenSnap/image02.png)
![image03](http://github.com/haoyuexing/RequestListView/raw/master/ScreenSnap/image03.png)

## 使用方法
下面展示一些Demo中的例子。并做出尽可能详细的说明。

**Json:**
```json
[
    {
        "name": "YosonHao-1",
        "email": "Haoyuexing@gmail.com-1"
    },
    {
        "name": "YosonHao-2",
        "email": "Haoyuexing@gmail.com-2"
    },
    {
        "name": "YosonHao-3",
        "email": "Haoyuexing@gmail.com-3"
    }
]
```
**Bean:**
```java
public class ArrayBean {

	private String name;
	private String email;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "ARArrayBean [name=" + name + ", email=" + email + "]";
	}

}
```
* 一个实体类，这个基本没什么可解释的。
* 实体类中toString()是可有可无的，有时候为了测试方便，我个人喜欢加上。

**Adapter:**
```java
public class ArrayAdapter extends BaseAdapter {

	ViewHolder mHolder;

	private Context mContext;
	private List<ArrayBean> mList = new ArrayList<ArrayBean>();

	public ArrayAdapter(List<ArrayBean> list, Context context) {
		super();
		this.mList = list;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {

		View view = arg1;

		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.item_array,
					null);
			mHolder = new ViewHolder();
			mHolder.name = (TextView) view.findViewById(R.id.name);
			mHolder.email = (TextView) view.findViewById(R.id.email);
			view.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}

		ArrayBean arrayBean = mList.get(arg0);
		if (arrayBean != null) {
			mHolder.name.setText("Name:" + arrayBean.getName());
			mHolder.email.setText("Email:" + arrayBean.getEmail());
		}

		return view;
	}

	static class ViewHolder {
		TextView name, email;
	}

}
```
* 自定义Adapter还是比较常用的。我个人常年使用自定义的。=.=
* 构造方法需要两个参数。一个是你每一个Item的实体的List，另一个则是上下文。
* Override的一堆方法，除了getView(int,View,ViewGroup)之外没什么好说的。
* inflate你的item布局，得到布局上的控件，为控件set上数据。
* 之前传进来了List，你的数据自然是从list.get(i)中得到的。
* 想效率稍微高一点儿，可以像我一样用ViewHolder，当然不用也是可以达到效果的。
* Adapter也没啥可说了，一个人有一个人的写法儿。

**Activity:**
```java
private AjaxListView mListView;
mListView = (RequestListView) findViewById(R.id.requestlv);
mListView.setAdapter(mAdapter);
		
mListView.setFooterBackgroundResource(R.drawable.footer_bg);
mListView.setFooterProgressDrawableResource(R.drawable.progress);
mListView.setFooterHint("正在加载...", "更多...");
mListView.setRequestType(RequestListView.TYPE_POST);
mListView.setPageName("page");
HashMap<String, String> params=new HashMap<String, String>();
params.put("参数名", "参数值");
mListView.showResult("http://gitdemo.duapp.com/RequestData",params);
mListView.setOnCompleteListener(new OnCompleteListener() {

	@Override
	public void onSuccess(String str) {
		mList.addAll(JSON.parseArray(str, ArrayBean.class));
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onFail(String str) {

	}
	
});
```
**下面是重点部分**
* showResult方法，是开始请求的入口，想有数据，就一定要调用这个方法。
* showResult有两个重载的方法```"RequestListView.showResult(url)"```和```"RequestListView.showResult(url,params)"```如果没有参数或者想直接把参数写在url上，都是可以的，直接调用前者。如果有参数就new一个HashMap来存参数，再传入后者即可。
* url是必填的。如果为null or empty都会抛出异常。
* 分页显示都有一个请求的页码的参数名，默认为```"page"```。比如，showResult重的url参数为```"www.baidu.com"```，若不setPageName，那么该请求地址实际为：```"www.baidu.com?page=1"```若setPageName为```"p"```，则请求地址为：```"www.baidu.com?p=1"```。点击更多的时候，1也会随之而变化。
* setRequestType方法，可以设置请求的类型。二选一。```"RequestListView.TYPE_GET"```和```"RequestListView.TYPE_POST"```，默认为get。
* setFooterHint方法，可更改footer的提示信息。默认普通提示为```"More..."```加载中为```"onLoading..."```
* setFooterBackgroundResource方法，可以用来设置你自己的footer背景，我这里用了一个xml来表示。默认为空白。
* setFooterProgressDrawableResource方法，可以用来设置你的进度条样式，其原理是一张图片在进行旋转，因此，你只需要传一张圆形的进度条图片就行，它会自己转的。
* setOnCompleteListener方法，请求以后的回调。onSuccess为成功，onFail为失败。
* onSuccess(str)，str为请求返回的数据。请自行解析。
* onFail(str)，str为失败的原因。这里没设计好，应改改成一个int类型的常量。暂时无视这个str就行，后面会改。

## 友情提示
```xml
<uses-permission android:name="android.permission.INTERNET" />
```
* 你懂的。

## 特别鸣谢
* 特别感谢三位大神，很庆幸认识这三位大神。
* 基本都是有问必答，而且都很有耐心。分分钟涨姿势。
* **Tank** —— [GitHub](https://github.com/TangKe)
* **Alex YU** —— [新浪微博](http://weibo.com/alexyuyxj)
* **任飘渺** —— [腾讯微博](http://t.qq.com/ymiou2008)
* 还有坦克的设计和大熊的测试，感谢二位。
* **Special** —— [花瓣](http://huaban.com/linbei003)
* **大熊** —— [新浪微博](http://weibo.com/u/2295430670)

## 关于作者
* 苦逼一个。快失业了，有介绍工作的咩？
* 新浪微博：[@郝悦兴](http://weibo.com/haoyuexing)

## 更新说明
**2013/08/28**
* 修复了一个对模拟器不支持的Bug。
* 添加了请求完成、请求错误的回调。

**2013/08/29**
* 添加了追加参数的方法。
* 修改了putUrlParaName方法名，改为putUrlPageParaName。
* 添加了修改footer的方法以及footer的默认值。
* 修改了putUrlPageParaName的方法参数，为其添加默认值。

**2013/09/11**
* 整个项目更名为RequestListView。删除自动解析的功能和相关方法，使用户能更自由的操作所返回的Json数据。

**2013/09/12**
* 将回调类型改为String。
* 添加了自定义footer背景的方法。
* 添加了自定义progress的方法。

**2013/10/15**
* 增加下拉刷新。

**2013/10/16**
* 修改下拉时松手不会弹的bug。

**2013/10/17**
* 最后更新时间改为24小时制。

**2014/04/28**
* 重新整理整个项目。
* 去除下拉刷新。
* 修改部分方法名称。
* 修复网络错误是页码数字不正确的情况。
* 增加setRequestType。
* 修改传入参数的方式。

**2014/04/29**
* 添加注释。
* 更新ReadMe。
* 修复TYPE_GET时链接拼凑错误的问题。
