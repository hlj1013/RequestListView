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
package com.hao.ajaxlistviewdemo;

import com.hao.ajaxlistview.AjaxListView;
import com.hao.ajaxlistviewdemo.adapter.ComplcatedAdapter;
import com.hao.ajaxlistviewdemo.bean.Teacher;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;

/**
 * 
 * @ClassName: ComplicatedActivity 
 * @Description: 相对复杂的Json
 * @Author Yoson Hao
 * @WebSite www.haoyuexing.cn
 * @Email haoyuexing@gmail.com
 * @Date 2013-8-27 下午5:31:36
 */
public class ComplicatedJsonActivity extends Activity {

	private AjaxListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_complicated);

		RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.complicated);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.bg);
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		bd.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		bd.setDither(true);
		relativeLayout.setBackgroundDrawable(bd);

		mListView = (AjaxListView) findViewById(R.id.lv);
		mListView
				.putUrl("http://gitdemo.duapp.com/AjaxListViewComplicatedData");
		mListView.putUrlParaName("page");
		mListView.putAdapter(ComplcatedAdapter.class);
		mListView.putBean(Teacher.class);
		mListView.showResult();

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Toast.makeText(ComplicatedJsonActivity.this, arg2 + "",
						Toast.LENGTH_SHORT).show();
			}
		});

	}

}
