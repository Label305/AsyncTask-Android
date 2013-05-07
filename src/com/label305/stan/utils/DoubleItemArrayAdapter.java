package com.label305.stan.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.label305.stan.R;

public abstract class DoubleItemArrayAdapter<T> extends ArrayAdapter<T> {

	private Context mContext;

	public DoubleItemArrayAdapter(Context context) {
		mContext = context;
	}

	@Override
	public int getCount() {
		return (int) Math.ceil(((double) super.getCount()) / 2);
	}

	public T getLeftItem(int position) {
		return getItem(position * 2);
	}

	public T getRightItem(int position) {
		return getItem(position * 2 + 1);
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
		leftViewGroup.addView(getItemView(getLeftItem(position), leftView, parent));

		ViewGroup rightViewGroup = (ViewGroup) rowView.findViewById(R.id.doubleitemarrayadapter_rowview_rightviewgroup);
		if (position * 2 + 1 < super.getCount()) {
			View rightView = rightViewGroup.getChildAt(0);
			rightViewGroup.removeAllViews();
			rightViewGroup.setVisibility(View.VISIBLE);
			rightViewGroup.addView(getItemView(getRightItem(position), rightView, parent));
		} else {
			rightViewGroup.setVisibility(View.INVISIBLE);
		}

		return rowView;
	}

	protected abstract View getItemView(T item, View convertView, ViewGroup parent);
}
