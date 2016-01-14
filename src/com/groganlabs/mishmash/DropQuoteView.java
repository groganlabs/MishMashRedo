package com.groganlabs.mishmash;

import java.util.ArrayList;

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
	
	int defaultCols = 10, actualCols, gameRows, letterRows;
	int sqPad = 2, lineWidth = 1;
	int mW, mH;
	int mHighlighted = -1;
	
	Paint mTextPaint, mBgPaint, mHiLightPaint;
	

	public DropQuoteView(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	private void init() {
		
		// minimum size for the game except in extreme cases
		minFontSize = 18 * mContext.getResources().getDisplayMetrics().density;
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setColor(Color.BLACK);
		
		mBgPaint = new Paint();
		//mBgPaint.setColor(0xff00ffff);
		mBgPaint.setColor(Color.WHITE);
		mBgPaint.setStyle(Paint.Style.FILL);
		
		mHiLightPaint = new Paint();
		mHiLightPaint.setColor(Color.YELLOW);
		mHiLightPaint.setStyle(Paint.Style.FILL);
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
		
		DropQuoteGame game = ((DropQuoteActivity) mContext).getGame();
		
		// If the default num of columns won't fit, readjust
		// should only happen on very small devices
		// Only do this on the first load, when game.letterCols = null
		if(game.letterCols == null) {
			if(defaultCols * (sqSize()) > w) {
				actualCols = (int) Math.ceil(game.getLength() / (sqSize() + lineWidth));
				gameRows = (int) Math.ceil(game.getLength() / actualCols);
				if(gameRows * 2 * (sqSize() + lineWidth) > h) {
					mFontSize = (float) Math.floor(h/(gameRows*2)) - 2*sqPad - lineWidth;
				}
				else {
					mFontSize = minFontSize;
				}
			}
			// Using default num of columns, stretch the game so it 
			// fills the height or width
			else {
				actualCols = defaultCols;
				gameRows = (int) Math.ceil(game.getLength() / defaultCols);
				
				float wSize = (float) Math.ceil(w / actualCols);
				float hSize = (float) Math.ceil(h / (gameRows * 2));
				
				if(wSize > hSize) {
					mFontSize = hSize - 2*sqPad - lineWidth;
				}
				else {
					mFontSize = wSize - 2*sqPad - lineWidth;
				}
			}
			
			game.initializeLetterCols(actualCols, gameRows);
			letterRows = 0;
			for(int ii = 0; ii < game.letterCols.size(); ii++) {
				if(((ArrayList) game.letterCols.get(ii)).size() > letterRows) {
					letterRows = ((ArrayList) game.letterCols.get(ii)).size();
				}
			}
		}
		// We've already setup the game, just need to resize it to fit
		else {
			float wSize = (float) Math.ceil(w / actualCols);
			float hSize = (float) Math.ceil(h / (gameRows * 2));
			
			if(wSize > hSize) {
				mFontSize = hSize - 2*sqPad - lineWidth;
			}
			else {
				mFontSize = wSize - 2*sqPad - lineWidth;
			}
		}
		
		mTextPaint.setTextSize(mFontSize);
		
		// Game takes up all available space
		setMeasuredDimension(w, h);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		float charWidth, charPadding;
		Paint current;
		DropQuoteGame game = ((DropQuoteGame)mContext).getGame();
		for(int ii = 0; ii < actualCols; ii++) {
			canvas.drawRect(ii * (sqSize()+lineWidth), 0, 
					ii * (sqSize()+lineWidth) + sqSize(), 
					sqSize()*letterRows,
					mBgPaint);
			
			// Draw the letter columns on top
			for(int jj = 0; jj < ((ArrayList<DqChar>)game.letterCols.get(ii)).size(); jj++) {
				charWidth = mTextPaint.measureText(((DqChar)((ArrayList)game.letterCols.get(ii)).get(jj)).getLetterArr(), 0, 1);
				charPadding = (mFontSize-charWidth)/2f;
				canvas.drawText(((DqChar)((ArrayList)game.letterCols.get(ii)).get(jj)).getLetterArr(),
						0, 1,
						ii * (sqSize()+lineWidth) + sqPad + charPadding,
						((letterRows-jj-1) * sqSize()) + sqPad,
						mTextPaint);
				if(((DqChar)((ArrayList)game.letterCols.get(ii)).get(jj)).isAnswerSet()) {
					// if mHighlight = answer, draw highlighted square
					canvas.drawLine(ii * (sqSize()+lineWidth) + sqPad,
							((letterRows-jj-1) * sqSize()) + sqPad,
							ii * (sqSize()+lineWidth+1) - sqPad,
							((letterRows-jj) * sqSize()) - sqPad,
							mTextPaint);
				}
			}
		}
		
		float lettersHeight = letterRows * sqSize();
		for(int ii = 0; ii < game.dqSolutionLength; ii++) {
			if(game.dqSolution[ii] != ' ') {
				if(ii == mHighlighted) {
					current = mHiLightPaint;
				}
				else {
					current = mBgPaint;
				}
				canvas.drawRect((ii%actualCols)*(sqSize() + lineWidth),
						lettersHeight + ((float) Math.floor(ii/gameRows) * (sqSize() + lineWidth)),
						(ii%actualCols)*(sqSize() + lineWidth) + sqSize(),
						lettersHeight + ((float) Math.floor(ii/gameRows) * (sqSize() + lineWidth) + sqSize()),
						current);
				if(game.answerArr[ii] >= 'A' && game.answerArr[ii] <= 'Z') {
					charWidth = mTextPaint.measureText(game.answerArr, ii, 1);
					charPadding = (mFontSize-charWidth)/2f;
					canvas.drawText(game.answerArr, ii, 1, 
							(ii%actualCols)*(sqSize() + lineWidth) + sqPad + charPadding,
							lettersHeight + ((float)Math.floor(ii/gameRows) * (sqSize() + lineWidth)) + sqPad,
							mTextPaint);
				}
			}
		}
	}

	public Boolean touched(float x, float y) {
		// view was touched in the character columns
		if(y < letterRows * sqSize()) {
			// There's no square selected
			if(mHighlighted == -1) return false;
			
			int row = letterRows -(int) Math.ceil(letterRows * sqSize()/y);
			int col = (int) Math.floor(actualCols * (sqSize() + lineWidth)/x);
			
			// The touch was in a different column than the selected square
			if(col != mHighlighted % actualCols) return false;
			
			DropQuoteGame game = ((DropQuoteActivity)mContext).mGame;

			// The touch was on the highlighted square
			if(((DqChar)((ArrayList)game.letterCols.get(col)).get(row)).getAnswer() == mHighlighted) {
				// remove the answer from the square
				game.answerArr[mHighlighted] = 0;
				mHighlighted = -1;
				((DqChar)((ArrayList)game.letterCols.get(col)).get(row)).clearAnswer();
			}
			
			// There was no letter in the spot touched
			if(((ArrayList)game.letterCols.get(col)).size() < row) return false;
			
			//By now we have a character in the same column as the selected square
			// that isn't the same as the current answer
			// Time to remove any linking answers (in the letterCol), link the new one
			// and add the new answer
			for(int ii = 0; ii <((ArrayList)game.letterCols.get(col)).size(); ii++) {
				if(((DqChar)((ArrayList)game.letterCols.get(col)).get(ii)).getAnswer() == mHighlighted) {
					((DqChar)((ArrayList)game.letterCols.get(col)).get(ii)).clearAnswer();
					break;
				}
			}
			game.answerArr[mHighlighted] = ((DqChar)((ArrayList)game.letterCols.get(col)).get(row)).getLetter();
			((DqChar)((ArrayList)game.letterCols.get(col)).get(row)).setAnswer(mHighlighted);
			return true;
		}
		// view was touched in the squares
		else if(y < (letterRows * (mFontSize + 2*sqPad) + 
				gameRows * (mFontSize + 2*sqPad + lineWidth))) {
			DropQuoteGame game = ((DropQuoteActivity)mContext).mGame;
			int row = letterRows -(int) Math.ceil(gameRows * (sqSize()+lineWidth)/(y - sqSize()*letterRows));
			int col = (int) Math.floor(actualCols * (sqSize() + lineWidth)/x);
			int index = row * actualCols + col;
			if(game.dqSolution[index] == ' ') return false;
			
			mHighlighted = index;
			return true;
		}
		return false;
		
	}
	
	private float sqSize() {
		return mFontSize + (2 * sqPad);
	}
	
	public void setHighlight(int hl) {
		mHighlighted = hl;
	}
}
