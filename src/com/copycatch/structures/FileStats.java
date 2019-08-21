package com.copycatch.structures;

import name.fraser.neil.plaintext.diff_match_patch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class FileStats
{
    static public double[][] scores;
    /* Fields */
    private String fileName;
    private ArrayList<String> outerCode;
    private ArrayList<ArrayList<String>> tokenizedFunctions;
    private int stringLength = 0;
    private int functionCount = 0;
    private boolean writingFunction;
    private char lastAddedChar;

    /* Constructor */
    public FileStats(String name)
    {
        this.fileName = name;
        outerCode = new ArrayList<>();
        tokenizedFunctions = new ArrayList<>();
    }

    // Initializes the score array after files have been scanned;
    public static void SetScoresSize(int size)
    {
        scores = new double[size][size];
        for (int i = 0; i < size; i++)
        {
            Arrays.fill(scores[i], -1);
        }
    }

    /* Functions */

    public static String[] GetLinesAsStringWithMatchedFunctions(FileStats f1, FileStats f2)
    {
        boolean swapped;
        int biggestIx = 0;
        LinkedList<diff_match_patch.Diff> diff;
        diff_match_patch dmp = new diff_match_patch();
        ArrayList<ArrayList<String>> funcs1;
        ArrayList<ArrayList<String>> funcs2;
        if (f1.tokenizedFunctions.size() > f2.tokenizedFunctions.size())
        {
            funcs1 = f2.tokenizedFunctions;
            funcs2 = f1.tokenizedFunctions;
            swapped = true;
        }
        else
        {
            funcs1 = f1.tokenizedFunctions;
            funcs2 = f2.tokenizedFunctions;
            swapped = false;
        }

        for (int i = 0; i < funcs1.size(); i++)
        {
            if (i == funcs2.size() - 1)
            {
                break;
            }
            int SmallestSoFar = Integer.MAX_VALUE;
            for (int j = i; j < funcs2.size(); j++)
            {
                diff = dmp.diff_main(ArrayListToString(funcs1.get(i)), ArrayListToString(funcs2.get(j)));
                int tmp = dmp.diff_levenshtein(diff);
                if (tmp < SmallestSoFar)
                {
                    SmallestSoFar = tmp;
                    biggestIx = j;
                }
            }
            if (i != biggestIx)
            {
                Collections.swap(funcs2, biggestIx, i);
            }
        }
        StringBuilder bld1 = new StringBuilder();
        StringBuilder bld2 = new StringBuilder();

        String[] strs = MatchOuterCode(f1.outerCode, f2.outerCode);
        bld1.append(strs[0]);
        bld2.append(strs[1]);
        if (!swapped)
        {
            bld1.append(FunctionListToString(funcs1));
            bld2.append(FunctionListToString(funcs2));
        }
        else
        {
            bld1.append(FunctionListToString(funcs2));
            bld2.append(FunctionListToString(funcs1));
        }

        return new String[]{bld1.toString(), bld2.toString()};
    }

    private static String[] MatchOuterCode(ArrayList<String> f1, ArrayList<String> f2)
    {
        int biggestIx = 0;
        LinkedList<diff_match_patch.Diff> diff;
        diff_match_patch dmp = new diff_match_patch();
        ArrayList<String> smaller;
        ArrayList<String> larger;
        boolean swapped = false;
        if (f1.size() > f2.size())
        {
            smaller = f2;
            larger = f1;
            swapped = true;
        }
        else
        {
            smaller = f1;
            larger = f2;
        }
        for (int i = 0; i < smaller.size(); i++)
        {
            if (i == larger.size() - 1)
            {
                break;
            }
            int SmallestSoFar = Integer.MAX_VALUE;
            for (int j = i; j < larger.size(); j++)
            {
                diff = dmp.diff_main(smaller.get(i), larger.get(j));
                int tmp = dmp.diff_levenshtein(diff);
                if (tmp < SmallestSoFar)
                {
                    SmallestSoFar = tmp;
                    biggestIx = j;
                }
            }
            if (i != biggestIx)
            {
                Collections.swap(larger, biggestIx, i);
            }
        }
        if (swapped)
        {
            return new String[]{ArrayListToString(larger), ArrayListToString(smaller)};
        }
        else
        {
            return new String[]{ArrayListToString(smaller), ArrayListToString(larger)};
        }
    }

    private static String ArrayListToString(ArrayList<String> arrayList)
    {
        StringBuilder stringBuilder = new StringBuilder();
		for (String string : arrayList)
		{
			stringBuilder.append(string);
		}
        return stringBuilder.toString();
    }

    private static String FunctionListToString(ArrayList<ArrayList<String>> arrayLists)
    {
        StringBuilder bld = new StringBuilder();
		for (ArrayList<String> stringArrayList : arrayLists)
		{
			for (String value : stringArrayList)
			{
				bld.append(value);
			}
		}
        return bld.toString();
    }

    private static int CountCharOccurrences(String s, char c)
    {
        int count = 0;
        for (int i = 0; i < s.length(); i++)
        {
            if (s.charAt(i) == c)
            {
                count++;
            }
        }
        return count;
    }

    public void setWritingFunction(boolean b)
    {
        writingFunction = b;
    }

    public void FunctionDone()
    {
        functionCount++;
    }

    public char GetLastAddedChar()
    {
        return lastAddedChar;
    }

    // Stores a tokenized line in the file's line list
    private void WriteLine(String str)
    {
        if (tokenizedFunctions.size() - 1 == functionCount)
        {
            tokenizedFunctions.get(functionCount).add(str);
        }
        else
        {
            tokenizedFunctions.add(new ArrayList<>());
            tokenizedFunctions.get(functionCount).add(str);
        }

    }

    public String ExtractLastOuterCodeLine()
    {
        if (!outerCode.isEmpty())
        {
            return outerCode.remove(outerCode.size() - 1);
        }
        else
        {
            return null;
        }
    }

    public String ExtractLastFunctionCodeLine()
    {
        if (!tokenizedFunctions.get(functionCount).isEmpty())
        {
            return tokenizedFunctions.get(functionCount).remove(tokenizedFunctions.get(functionCount).size() - 1);
        }
        else
        {
            return null;
        }
    }

    public boolean OuterCodeIsEmpty()
    {
        return outerCode.isEmpty();
    }

    // Master function for converting files:
    // Writes the line to the Lines List and Hashes the tokens
    public void HandleLine(String str)
    {
        if (!writingFunction)
        {
            outerCode.add(str);
        }
        else
        {
            WriteLine(str);
        }
        setStringLength(getStringLength() + str.length());
        if (str.length() > 0)
        {
            lastAddedChar = str.charAt(str.length() - 1);
        }
    }

	public void SortFunctionsByLength()
    {
        for (int i = 0; i < tokenizedFunctions.size() - 1; i++)
        {
            for (int j = i + 1; j < tokenizedFunctions.size(); j++)
            {
                if (GetFunctionLength(i) < GetFunctionLength(j))
                {
                    Collections.swap(tokenizedFunctions, i, j);
                    // tokenizedLines.add(j,tokenizedLines.remove(i));
                }
            }
        }
    }

    private int GetFunctionLength(int ix)
    {
        int size = tokenizedFunctions.get(ix).size();
        int length = 0;
        for (int i = 0; i < size; i++)
        {
            length += tokenizedFunctions.get(ix).get(i).length();
        }
        return length;
    }

	private int getStringLength()
    {
        return stringLength;
    }

    private void setStringLength(int i)
    {
        stringLength += i;
    }

    public void OrganizeOuterCode()
    {
        ArrayList<String> organized = new ArrayList<>();
        String str = "";
        boolean lookingForSemiColon = false;
		for (String s : outerCode)
		{
			str = str + s;
			if (!lookingForSemiColon)
			{
				if (!str.contains(";"))
				{
					lookingForSemiColon = true;
				}
				else if (CountCharOccurrences(str, ';') > 1)
				{
					// Need to split.
				}
				else
				{
					organized.add(str);
					str = "";
				}
			}
			if (str.contains(";") && lookingForSemiColon)
			{
				int j;
				for (j = 0; j < str.length(); j++)
				{
					if (str.charAt(j) == ';')
					{
						j++;
						break;
					}
				}
				organized.add(str.substring(0, j));
				str = str.substring(j);
				if (str.isEmpty() || str.contains(";"))
				{
					lookingForSemiColon = false;
					if (!str.isEmpty())
					{
						organized.add(str);
						str = "";
					}
				}
			}
		}
        outerCode = organized;
    }

}
