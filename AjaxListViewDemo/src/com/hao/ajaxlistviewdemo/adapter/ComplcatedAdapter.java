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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hao.ajaxlistviewdemo.R;
import com.hao.ajaxlistviewdemo.bean.Teacher;

/**
 * 
 * @ClassName: ComplcatedAdapter 
 * @Description: ComplcatedAdapter
 * @Author Yoson Hao
 * @WebSite www.haoyuexing.cn
 * @Email haoyuexing@gmail.com
 * @Date 2013-8-28 上午10:46:19
 */
public class ComplcatedAdapter extends BaseAdapter {

	ViewHolder mHolder;

	private Context mContext;
	private List<Teacher> mList = new ArrayList<Teacher>();

	public ComplcatedAdapter(List<Teacher> list, Context context) {
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
			view = LayoutInflater.from(mContext).inflate(
					R.layout.complicated_item, null);
			mHolder = new ViewHolder();
			mHolder.teacher_name = (TextView) view
					.findViewById(R.id.teacher_name);
			mHolder.teacher_age = (TextView) view
					.findViewById(R.id.teacher_age);
			mHolder.student_name = (TextView) view
					.findViewById(R.id.student_name);
			mHolder.student_weibo = (TextView) view
					.findViewById(R.id.student_weibo);
			view.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}

		Teacher teacher = mList.get(arg0);
		if (teacher != null) {
			mHolder.teacher_name.setText("Name:" + teacher.getName());
			mHolder.teacher_age.setText("Age:" + teacher.getAge());
			if (teacher.getStudent() != null) {
				mHolder.student_name.setText("Name:"
						+ teacher.getStudent().getName());
				mHolder.student_weibo.setText("Weibo:"
						+ teacher.getStudent().getWeibo());
			}
		}

		return view;
	}

	static class ViewHolder {
		TextView teacher_name, teacher_age, student_name, student_weibo;
	}
}
