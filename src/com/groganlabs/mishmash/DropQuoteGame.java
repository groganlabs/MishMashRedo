package com.groganlabs.mishmash;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.os.Parcel;

public class DropQuoteGame extends Game {
	
	protected char[] dqSolution;
	protected int dqSolutionLength;
	
	// 2d array of letters for the top of the puzzle
	// contains DqChar objects
	protected ArrayList letterCols;
	
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
	
	public void initializeLetterCols(int cols, int rows) {
		// TODO: Remove ability to resize
		//Check to make sure we're actually changing the size
		if(letterCols != null & letterCols.size() == cols)
			return;
		
		ArrayList tempLetterArray = new ArrayList(cols);
		
		for(int ii = 0; ii < cols; ii++) {
			tempLetterArray[ii] = new ArrayList(rows);
		}
		
		for(int ii=0; ii < dqSolutionLength; ii++) {
			if(dqSolution[ii] != ' ') {
				tempLetterArray[ii%cols].add(new DqChar(dqSolution[ii]));
			}
		}
		
		// Shuffle the columns
		Random random = new Random();
		for(int ii = 0; ii < cols; ii++) {
			Collections.shuffle(tempLetterArray[ii], random);
		}
		
		int tempAnswer;
		char tempLetter;
		
		// if we already had a letterCol array, we might have had answers stored in there
		if(letterCols != null) {
			// Loop through the nested arrays
			for(int ii = 0; ii < letterCols.size(); ii++) {
				for(int jj = 0; jj < letterCols[ii].size(); jj++) {
					// If there is an answer
					if(letterCols[ii][jj].isAnswerSet()) {
						tempAnswer = letterCols[ii][jj].getAnswer();
						tempLetter = letterCols[ii][jj].getLetter();
						// Look through the new column for an equal but unused letter
						int newIndex = tempLetterArray[tempAnswer%cols].indexOf(new DqChar(tempLetter));
						if(newIndex != -1) {
							tempLetterArray[tempAnswer%cols][newIndex].setAnswer(tempAnswer);
						}
						else {
							Log.d("DQGame", "Error: Couldn't find a free letter");
						}
					}
				}
			}
		}
		
		letterCols = tempLetterArray;
	}
	
	public int getGameType() {
		return DROP_GAME;
	}

	public int getLength() {
		return dqSolutionLength;
	}
}
