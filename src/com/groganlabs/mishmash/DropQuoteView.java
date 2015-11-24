package com.groganlabs.mishmash;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.View.MeasureSpec;

public class DropQuoteView extends View {
	Context mContext;
	
	float mFontSize, minFontSize;
	
	int defaultCols = 10, actualCols, numRows;
	int sqPad = 2, lineWidth = 1;
	int mW, mH;
	
	Paint mTextPaint, mBgPaint;
	

	public DropQuoteView(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	private void init() {
		
		// minimum size for the game except in extreme cases
		minFontSize = 18 * mContext.getResources().getDisplayMetrics().density;
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setColor(0xffffffff);
		
		mBgPaint = new Paint();
		//mBgPaint.setColor(0xff00ffff);
		mBgPaint.setColor(Color.BLUE);
		mBgPaint.setStyle(Paint.Style.FILL);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mW = w;
	}
	
	/**
	 * Establishing the size of the View and the characters 
	 */
	@Override
	protected void onMeasure(int wMS, int hMS) {
		int w = MeasureSpec.getSize(wMS);
		int h = MeasureSpec.getSize(hMS);
		
		float sqSize = minFontSize + sqPad*2 + lineWidth;
		DropQuoteGame game = ((DropQuoteActivity) mContext).getGame();
		
		// If the default num of columns won't fit, readjust
		// should only happen on very small devices
		if(defaultCols * (sqSize) - lineWidth > w) {
			actualCols = (int) Math.ceil(game.getLength() / (sqSize));
			numRows = (int) Math.ceil(game.getLength() / actualCols) * 2;
			if(numRows * sqSize > h) {
				mFontSize = (float) Math.floor(h/numRows) - 2*sqPad - lineWidth;
			}
			else {
				mFontSize = minFontSize;
			}
		}
		// Using default num of columns, stretch the game so it 
		// fills the height or width
		else {
			actualCols = defaultCols;
			numRows = (int) Math.ceil(game.getLength() / defaultCols) * 2;
			
			float wSize = (float) Math.ceil(w / actualCols);
			float hSize = (float) Math.ceil(h / numRows);
			
			if(wSize > hSize) {
				mFontSize = hSize - 2*sqPad - lineWidth;
			}
			else {
				mFontSize = wSize - 2*sqPad - lineWidth;
			}
		}
		
		// Game takes up all available space
		setMeasuredDimension(w, h);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
	}
}
