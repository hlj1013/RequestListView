RequestListView
============

* RequestListView是基于ListView的控件。
* 通过少量参数达到分页效果。
* 请求源于网络的Json数据。
* 支持请求完成、请求错误的回调。

```java
private AjaxListView mListView;

mListView = (RequestListView) findViewById(R.id.requestlv);
mListView.setUrl("http://gitdemo.duapp.com/RequestData");
mListView.setAdapter(mAdapter);
mListView.setOnCompleteListener(new OnCompleteListener() {

	@Override
	public void onSuccess(String str) {
		// TODO Auto-generated method stub
				
	}

	@Override
	public void onFail() {
		// TODO Auto-generated method stub
				
	}

});
mListView.showResult();
```
## 效果图

![image01](http://github.com/haoyuexing/RequestListView/raw/master/ScreenShot/image01.png)
![image02](http://github.com/haoyuexing/RequestListView/raw/master/ScreenShot/image02.png)
![image03](http://github.com/haoyuexing/RequestListView/raw/master/ScreenShot/image03.png)

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
mListView.setUrl("http://gitdemo.duapp.com/RequestData");
mListView.setAdapter(mAdapter);
mListView.setUrlPageParaName("page");
mListView.setUrlPara("peopleId", "1");
mListView.setFooterHint("更多...", "加载中...");
mListView.setFooterBackground(R.drawable.footer_bg);
mListView.setProgressDrawable(R.drawable.progress);
mListView.setOnCompleteListener(new OnCompleteListener() {

	@Override
	public void onFail() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSuccess(String str) {
		// TODO Auto-generated method stub

	}

});
mListView.showResult();
```
**下面是重点部分**
* setUrl传一个你需要请求的网络地址。
* setUrlPageParaName传一个你请求的页码的参数名。默认值为```"page"```
* 比如，setUrl为```"www.baidu.com"```，putUrlPageParaName```"page"```。
* 其实际请求地址为：```"www.baidu.com?page=1"```点击更多是时，1也会随之而变化。
* 若setUrlPara传入```"peopleId"```和```"1"```则表示在上继续添加参数。
* 地址即为：```"www.baidu.com?page=1&peopleId=1"```
* setFootHint方法可更改footer的提示信息。默认普通提示为```"More..."```加载中为```"onLoading..."```
* setFooterBackground方法可以用来设置你自己的footer背景，我这里用了一个xml来表示。默认为空白。
* setProgressDrawable方法可以用来设置你的进度条样式，其原理是一张图片在进行旋转，因此，你只需要传一张圆形的进度条图片就行，它会自己转的。
* setOnCompleteListener方法，这个很明显是请求完成以后的回调了，在onSuccess中会带进来一个Sring类型的str参数，这个参数就是你请求以后的结果，也就是你所请求的json数据。onFail的是失败的毁掉，比如没有网络之类的。这两个，请大家自行处理。
* 最后记得执行一下showResult来运行整个过程。

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
