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
package com.hao.requestlistviewdemo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.hao.requestlistview.RequestListView;
import com.hao.requestlistview.RequestListView.OnCompleteListener;

public class MainActivity extends Activity {

	private RequestListView mListView;

	private List<ArrayBean> mList;

	private ArrayAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mList = new ArrayList<ArrayBean>();
		mAdapter = new ArrayAdapter(mList, this);

		mListView = (RequestListView) findViewById(R.id.requestlv);
		mListView.setUrl("http://gitdemo.duapp.com/RequestData");
		mListView.setAdapter(mAdapter);
		mListView.setUrlPageParaName("page");
		mListView.setUrlPara("peopleId", "1");
		mListView.setFooterHint("更多...", "加载中...");
		mListView.setFooterDrawable(R.drawable.progress);
		mListView.setOnCompleteListener(new OnCompleteListener() {

			@Override
			public void onFail() {
				
			}

			@Override
			public void onSuccess(Object obj) {
				mList.addAll(JSON.parseArray(obj.toString(), ArrayBean.class));
				mAdapter.notifyDataSetChanged();
			}

		});
		mListView.showResult();
	}
}