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
package com.hao.ajaxlistviewdemo.adapter;

import java.util.ArrayList;
import java.util.List;

import com.hao.ajaxlistviewdemo.R;
import com.hao.ajaxlistviewdemo.bean.People;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 
 * @ClassName: SimpleAdapter 
 * @Description: SimpleAdapter
 * @Author Yoson Hao
 * @WebSite www.haoyuexing.cn
 * @Email haoyuexing@gmail.com
 * @Date 2013-8-27 下午2:56:42
 */
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
