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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewGroup view = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.doubleitemarrayadapter_rowview, parent, false);

		ViewGroup leftViewGroup = (ViewGroup) view.findViewById(R.id.doubleitemarrayadapter_rowview_leftviewgroup);
		leftViewGroup.addView(getItemView(getLeftItem(position), leftViewGroup));

		ViewGroup rightViewGroup = (ViewGroup) view.findViewById(R.id.doubleitemarrayadapter_rowview_rightviewgroup);
		if (position * 2 + 1 < super.getCount()) {
			rightViewGroup.setVisibility(View.VISIBLE);
			rightViewGroup.addView(getItemView(getRightItem(position), leftViewGroup));
		} else {
			rightViewGroup.setVisibility(View.INVISIBLE);
		}

		return view;
	}

	protected abstract View getItemView(T item, ViewGroup parent);
}
