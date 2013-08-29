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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
	private BaseAdapter mAdapter;

	private String mUrl;
	private String mUrlPageParaName;

	private Class<?> mBeanClass;
	private Class<?> mAdapterClass;

	private int mPageCount;

	private AQuery mAq;

	private HashMap<String, String> mParaMap;

	private OnAjaxCompleteListener mOnAjaxCompleteListener;
	private OnAjaxErrorListener mOnAjaxErrorListener;

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
		mParaMap = new HashMap<String, String>();
	}

	@Override
	public void onClick(View arg0) {
		mMore.setText("onLoading...");
		mMore.setClickable(false);
		ajax(++mPageCount);
	}

	public void showResult() {
		mAq = new AQuery(mContext);
		mList = new ArrayList<Object>();
		try {
			mAdapter = (BaseAdapter) mAdapterClass.getConstructor(List.class,
					Context.class).newInstance(mList, mContext);
		} catch (Exception e) {
			Log.w(mContext.getPackageName(), e.getMessage());
		}
		this.setAdapter(mAdapter);
		ajax(++mPageCount);
	}

	private void ajax(int pageCount) {
		String url = mUrl + "?" + mUrlPageParaName + "=" + pageCount;
		if (mParaMap.size() != 0) {
			Set<Map.Entry<String, String>> mapEntrySet = mParaMap.entrySet();
			Iterator<Map.Entry<String, String>> mapEntryIterator = mapEntrySet
					.iterator();
			while (mapEntryIterator.hasNext()) {
				Map.Entry<String, String> entry = mapEntryIterator.next();
				url = url + "&" + entry.getKey() + "=" + entry.getValue();
			}
		}
		System.out.println(url);
		mAq.ajax(url, String.class, new AjaxCallback<String>() {
			@Override
			public void callback(String url, String str, AjaxStatus status) {
				if (str != null) {
					Log.i(this.toString(), str);
					mList.addAll(JSON.parseArray(str, mBeanClass));
					mAdapter.notifyDataSetChanged();
					onComplete();
				} else {
					onAjaxError();
				}
				super.callback(url, str, status);
			}
		});
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

	public void putUrlPageParaName(String urlPageParaName) {
		this.mUrlPageParaName = urlPageParaName;
	}

	public void putUrlPara(String k, String v) {
		mParaMap.put(k, v);
	}

	private void onComplete() {
		mMore.setText("More...");
		mMore.setClickable(true);
		if (mOnAjaxCompleteListener != null) {
			mOnAjaxCompleteListener.onAjaxComplete();
		}
	}

	public void onAjaxError() {
		mMore.setText("More...");
		mMore.setClickable(true);
		if (mOnAjaxErrorListener != null) {
			mOnAjaxErrorListener.onAjaxError();
		}
	}

	public void setOnAjaxCompleteListener(OnAjaxCompleteListener l) {
		mOnAjaxCompleteListener = l;
	}

	public void setOnAjaxErrorListener(OnAjaxErrorListener l) {
		mOnAjaxErrorListener = l;
	}

	public interface OnAjaxCompleteListener {
		public void onAjaxComplete();
	}

	public interface OnAjaxErrorListener {
		public void onAjaxError();
	}

}
