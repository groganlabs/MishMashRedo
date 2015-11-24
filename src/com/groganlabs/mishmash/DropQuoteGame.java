package com.groganlabs.mishmash;

import java.util.Vector;

import android.content.Context;
import android.os.Parcel;

public class DropQuoteGame extends Game {
	
	protected char[] dqSolution;
	protected int dqSolutionLength;

	public DropQuoteGame(int game, int pack, Context context) throws Exception {
		super(game, pack, context);
		// TODO Auto-generated constructor stub
	}
	
	public DropQuoteGame(Parcel in) {
		super(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	protected void createGame() {
		int counter = 0;
		for(int ii = 0; ii < solutionArr.length; ii++) {
			if((solutionArr[ii] >= 'A' && solutionArr[ii] <= 'Z') || solutionArr[ii] == ' ') {
				dqSolution[counter] = solutionArr[ii];
				counter++;
			}
			
			if(solutionArr[ii] == ' ') {
				answerArr[counter - 1] = ' ';
			}
		}
		
		dqSolutionLength = counter;
		
	}

	@Override
	public void clearAnswer() {
		for(int ii = 0; ii < dqSolutionLength; ii++) {
			if(answerArr[ii] >= 'A' && answerArr[ii] <= 'Z') {
				answerArr[ii] = 0;
			}
		}
	}
	
	public int getGameType() {
		return DROP_GAME;
	}

	public int getLength() {
		return dqSolutionLength;
	}
}
