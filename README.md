AjaxListView
============

* AjaxListView是基于ListView的控件。
* 通过少量参数达到分页效果。
* 请求源于网络的Json。
* 解析获取的Json数据。
* 支持请求完成、请求错误的回调。

```java
private AjaxListView mListView;
mListView = (AjaxListView) findViewById(R.id.lv);
mListView.putUrl("http://gitdemo.duapp.com/AjaxListViewSimpleData");
mListView.putUrlPageParaName("page");
mListView.putAdapter(SimpleAdapter.class);
mListView.putBean(People.class);
mListView.showResult();
```
## 使用方法

下面展示一些Demo中的例子。并做出尽可能详细的说明。

**Json:**
```json
[
  {
    "firstName": "Hao-1",
    "lastName": "Yoson-1",
    "email": "Haoyuexing@gmail.com"
  },
  {
    "firstName": "Hao-2",
    "lastName": "Yoson-2",
    "email": "Haoyuexing@gmail.com"
  },
  {
    "firstName": "Hao-3",
    "lastName": "Yoson-3",
    "email": "Haoyuexing@gmail.com"
  }
]
```
**Bean:**
```java
public class People {

	private String firstName;
	private String lastName;
	private String email;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "People [firstName=" + firstName + ", lastName=" + lastName
				+ ", email=" + email + "]";
	}

}
```
* 控件集成了fastjson包。因为是自动解析，所以实体类的格式应该与你所请求的Json数据对应
* 不难看出，这段Json是一个JsonArray，里面包含着很多‘people’实体。
* 所以我们需要建立一个对应的People的实体类（当然这个名字，your wish。），字段名与Json中的Key相同即可。
* 实体类中toString()是可有可无的，有时候为了测试方便，我个人喜欢加上。

**Adapter:**
```java
public class SimpleAdapter extends BaseAdapter {

	ViewHolder mHolder;

	private Context mContext;
	private List<People> mList = new ArrayList<People>();

	public SimpleAdapter(List<People> list, Context context) {
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
			view = LayoutInflater.from(mContext).inflate(R.layout.simple_item,
					null);
			mHolder = new ViewHolder();
			mHolder.firstName = (TextView) view.findViewById(R.id.firstName);
			mHolder.lastName = (TextView) view.findViewById(R.id.lastName);
			mHolder.email = (TextView) view.findViewById(R.id.email);
			view.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}

		People people = mList.get(arg0);
		if (people != null) {
			mHolder.firstName.setText("FirstName:" + people.getFirstName());
			mHolder.lastName.setText("LastName:" + people.getLastName());
			mHolder.email.setText("Email:" + people.getEmail());
		}

		return view;
	}

	static class ViewHolder {
		TextView firstName, lastName, email;
	}
	
}
```
* 自定义Adapter还是比较常用的。我个人常年使用自定义的。=.=
* 构造方法需要两个参数。一个是你每一个Item的实体的List，另一个则是上下文。
* Override的一堆方法，除了getView(int,View,ViewGroup)之外没什么好说的。
* inflate你的item布局，得到布局上的控件，为控件set上数据。
* 之前传进来了List，你的数据自然是从list.get(i)中得到的。
* 想效率稍微高一点儿，可以像我一样用ViewHolder，当然不用也是可以达到效果的。

**Activity:**
```java
private AjaxListView mListView;
mListView = (AjaxListView) findViewById(R.id.lv);
mListView.putUrl("http://gitdemo.duapp.com/AjaxListViewSimpleData");
mListView.putUrlPageParaName("page");
mListView.putUrlPara("peopleId", "1");
mListView.putAdapter(SimpleAdapter.class);
mListView.putBean(People.class);
mListView.showResult();
```
* putUrl传一个你需要请求的网络地址。
* putUrlParaName传一个你请求的页码的参数名。
* 比如，putUrl为```"www.baidu.com"```，putUrlPageParaName```"page"```。
* 其实际请求地址为：```"www.baidu.com?page=1"```点击更多是1也会随之而变化。
* 若putUrlPara传入```"peopleId"```和```"1"```则表示在上继续添加参数。
* 地址即为：```"www.baidu.com?page=1&peopleId=1"```
* putAdapter，putBean直接传递进来对应的class就可以了。
* 最后执行一下showResult来显示数据。

## 友情提示
```xml
<uses-permission android:name="android.permission.INTERNET" />
```
* 你懂的。

## 关于

* 新浪微博：[@郝悦兴](http://weibo.com/haoyuexing)

## 更新说明
**2013/08/28**
* 修复了一个对模拟器不支持的Bug。
* 添加了请求完成、请求错误的回调。

**2013/08/29**
* 添加了追加参数的方法。
* 修改putUrlParaName方法名，改为putUrlPageParaName。


  
