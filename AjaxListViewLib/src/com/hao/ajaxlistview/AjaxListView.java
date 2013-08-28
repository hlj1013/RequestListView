/**
 * Copyright (c) 2013, Yoson Hao 郝悦兴 (haoyuexing@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hao.ajaxlistview;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

/**
 * 
 * @ClassName: AjaxListView 
 * @Description: List控件
 * @Author Yoson Hao
 * @WebSite www.haoyuexing.cn
 * @Email haoyuexing@gmail.com
 * @Date 2013-8-27 下午2:58:27
 */
public class AjaxListView extends ListView implements OnClickListener {

	private Context mContext;
	private LayoutInflater mInflater;
	private RelativeLayout mFooterView;
	private TextView mMore;

	private List<Object> mList;
	private ListAdapter mAdapter;

	private String mUrl;
	private String mUrlParaName;

	private Class<?> mBeanClass;
	private Class<?> mAdapterClass;

	private int mPageCount;

	private AQuery aq;

	public AjaxListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	public AjaxListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public AjaxListView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}

	private void init() {
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mFooterView = (RelativeLayout) mInflater.inflate(R.layout.footer, this,
				false);
		this.addFooterView(mFooterView);
		mMore = (TextView) mFooterView.findViewById(R.id.more);
		mMore.setText("onLoading...");
		mMore.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		mMore.setText("onLoading...");
		mMore.setClickable(false);
		ajax(++mPageCount);
	}

	public void showResult() {
		aq = new AQuery(mContext);
		mList = new ArrayList<Object>();
		try {
			mAdapter = (ListAdapter) mAdapterClass.getConstructor(List.class,
					Context.class).newInstance(mList, mContext);
		} catch (Exception e) {
			Log.w(mContext.getPackageName(), e.getMessage());
		}
		this.setAdapter(mAdapter);
		ajax(++mPageCount);
	}

	private void ajax(int pageCount) {
		String url = mUrl + "?" + mUrlParaName + "=" + pageCount;
		aq.ajax(url, String.class, new AjaxCallback<String>() {
			@Override
			public void callback(String url, String str, AjaxStatus status) {
				if (str != null) {
				Log.i(this.toString(), str);
					mList.addAll(JSON.parseArray(str, mBeanClass));
					onComplete();
				} else {
					Toast.makeText(mContext, "ajax error", Toast.LENGTH_SHORT)
							.show();
				}
				super.callback(url, str, status);
			}
		});
	}

	private void onComplete() {
		mMore.setText("More...");
		mMore.setClickable(true);
	}

	public void putAdapter(Class<?> adapterClass) {
		this.mAdapterClass = adapterClass;
	}

	public void putBean(Class<?> beanClass) {
		this.mBeanClass = beanClass;
	}

	public void putUrl(String url) {
		this.mUrl = url;
	}

	public void putUrlParaName(String urlParaName) {
		this.mUrlParaName = urlParaName;
	}
}
