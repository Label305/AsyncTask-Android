package com.label305.stan.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.label305.stan.R;

public abstract class DoubleItemArrayAdapter<T> extends ArrayAdapter<T> {

	private Context mContext;
	private AdapterView<?> mAdapterView;
	private AdapterView.OnItemClickListener mOnItemClickListener;

	public DoubleItemArrayAdapter(Context context, AdapterView<?> adapterView, AdapterView.OnItemClickListener onItemClickListener) {
		mContext = context;
		mAdapterView = adapterView;
		mOnItemClickListener = onItemClickListener;
	}

	@Override
	public int getCount() {
		return (int) Math.ceil(((double) super.getCount()) / 2);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public Context getContext() {
		return mContext;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewGroup rowView = (ViewGroup) convertView;

		if (rowView == null) {
			rowView = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.doubleitemarrayadapter_rowview, parent, false);
		}

		ViewGroup leftViewGroup = (ViewGroup) rowView.findViewById(R.id.doubleitemarrayadapter_rowview_leftviewgroup);
		View leftView = leftViewGroup.getChildAt(0);
		leftViewGroup.removeAllViews();
		leftView = getItemView(position * 2, leftView, parent);
		leftViewGroup.addView(leftView);
		leftView.setOnClickListener(new ItemOnClickListener(mOnItemClickListener, mAdapterView, position * 2, getItemId(position * 2)));
		leftView.setClickable(true);

		ViewGroup rightViewGroup = (ViewGroup) rowView.findViewById(R.id.doubleitemarrayadapter_rowview_rightviewgroup);
		if (position * 2 + 1 < super.getCount()) {
			View rightView = rightViewGroup.getChildAt(0);
			rightViewGroup.removeAllViews();
			rightViewGroup.setVisibility(View.VISIBLE);
			rightView = getItemView(position * 2 + 1, rightView, parent);
			rightViewGroup.addView(rightView);
			rightView.setOnClickListener(new ItemOnClickListener(mOnItemClickListener, mAdapterView, position * 2 + 1, getItemId(position * 2 + 1)));
			rightView.setClickable(true);
		} else {
			rightViewGroup.setVisibility(View.INVISIBLE);
		}

		return rowView;
	}

	protected abstract View getItemView(int position, View convertView, ViewGroup parent);

	private static class ItemOnClickListener implements View.OnClickListener {

		private AdapterView.OnItemClickListener mOnClickListener;
		private AdapterView<?> mAdapterView;
		private int mPosition;
		private long mId;

		public ItemOnClickListener(AdapterView.OnItemClickListener onItemClickListener, AdapterView<?> adapterView, int position, long id) {
			mOnClickListener = onItemClickListener;
			mAdapterView = adapterView;
			mPosition = position;
			mId = id;
		}

		@Override
		public void onClick(View v) {
			mOnClickListener.onItemClick(mAdapterView, v, mPosition, mId);
		}
	}
}
