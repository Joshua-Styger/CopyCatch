package com.copycatch.processing;


//Converts files to a generalized version of the file

import com.copycatch.structures.FileStats;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class ConvertFile
{
	// Takes a file to be read through and converted into a generalized version of
	// the file.
	// Will read line by line through file and call whichever function is suitable
	// for that situation
	// Writes generalized version to a new file
	private static ArrayList<String> keywords = new ArrayList<>();
	private static ArrayList<String> types = new ArrayList<>();
	private static HashMap<String, Character> commonKeywords;
	private static FileStats currentFileStats;
	private static boolean inCommentBlock;
	private static boolean inFunction;
	private static int functionBracketTracker;

	// FileStatsVersion
	public static void textConverter(File assignment, FileStats fileStats)
	{
		// Helps keep track of recurring variables in file.
		inFunction = false;
		functionBracketTracker = 0;
		inCommentBlock = false;

		Scanner scanner;
		StringBuilder builder = new StringBuilder();
		try
		{
			scanner = new Scanner(assignment);
		}
		catch (FileNotFoundException e)
		{
			System.out.println(assignment.getAbsolutePath() + " does not exist. Skipping...");
			return;
		}
		currentFileStats = fileStats;
		String currentLine;
		while (scanner.hasNextLine())
		{
			currentLine = scanner.nextLine();
			if (!currentLine.isEmpty())
			{
				ProcessLine(currentLine, builder);
				if (builder.length() != 0)
				{
					currentFileStats.HandleLine(builder.toString());
				}
				builder.setLength(0);
			}
		}
		currentFileStats.SortFunctionsByLength();
		currentFileStats.OrganizeOuterCode();
		currentFileStats = null;
		scanner.close();
	}

	private static void ProcessLine(String line, StringBuilder output)
	{
		int currentCharIndex = 0;
		int tempIndex;

		// Loop through line
		while (currentCharIndex < line.length())
		{
			// Get current char
			char c = line.charAt(currentCharIndex);
			if (!inCommentBlock)
			{
				if ((Character.isLetter(c) || c == '_'))
				{
					String str = "";
					if (currentCharIndex + 1 <= line.length() - 1)
					{
						char nextc = line.charAt(currentCharIndex + 1);
						tempIndex = currentCharIndex + 1;
						while ((Character.isLetter(nextc) || nextc == '_' || Character.isDigit(nextc))
								&& tempIndex < line.length())
						{
							tempIndex++;
							if (tempIndex < line.length())
							{
								nextc = line.charAt(tempIndex);
							}
						}
						str = line.substring(currentCharIndex, tempIndex);
					}
					else
					{
						str = str + c;
						tempIndex = currentCharIndex+1;
					}

					// Make sure to ignore extra spaces before checking next character
					if (isKeyword(str))
					{
						if (isType(str))
						{
							if (isNumberType(str))
							{
								output.append("n");
							}
							else
							{
								output.append("t");
							}
						}
						else
						{
							output.append(GetKeywordChar(str));
						}
					}
					else
					{
						if (tempIndex < line.length() && line.charAt(tempIndex) == '(')
						{
							output.append("f");
						}
						else if (tempIndex + 1 < line.length() && line.charAt(tempIndex + 1) == '(')
						{
							output.append("f");
							tempIndex++;
						}
						else
						{
							output.append("v");
						}
					}
					currentCharIndex = tempIndex;
				}
				else if (Character.isDigit(c))
				{
					tempIndex = currentCharIndex + 1;
					boolean decimalFound = false;
					while (tempIndex < line.length() && (Character.isDigit(line.charAt(tempIndex))
							|| (line.charAt(tempIndex) == '.' && !decimalFound)))
					{
						if (line.charAt(tempIndex) == '.')
						{
							decimalFound = true;
						}
						tempIndex++;
					}
					output.append("d");
					currentCharIndex = tempIndex;
				}

				else if (c == ' ' || c == '\t' || c == '{' || c == '}' || c == ',')
				{
					currentCharIndex++;
					if (c == '{')
					{
						if (!inFunction)
						{
							char ch = currentFileStats.GetLastAddedChar();
							StringBuilder tmp = new StringBuilder(output.toString());
							if (ch == ';' || ch == '}' || ch == 0)
							{
								// No action needed
							}
							else if (tmp.toString().contains(";") || tmp.toString().contains("}"))
							{
								// Send-off substring, then proceed
								int i;
								for (i = tmp.length() - 1; i >= 0; i--)
								{
									if (tmp.charAt(i) == '}' || tmp.charAt(i) == ';')
									{
										i++;
										break;
									}
								}
								currentFileStats.HandleLine(tmp.substring(0, i));
								output.setLength(0);
								output.append(tmp.substring(i));
							}
							else
							{
								// Find last statement or block code end, handle substring if needed, proceed
								while (!tmp.toString().contains(";") && !tmp.toString().contains("}") && !currentFileStats.OuterCodeIsEmpty())
								{
									tmp.insert(0, currentFileStats.ExtractLastOuterCodeLine());
								}
								// Send-off substring, then proceed
								int i;
								for (i = tmp.length() - 1; i >= 0; i--)
								{
									if (tmp.charAt(i) == '}' || tmp.charAt(i) == ';')
									{
										i++;
										break;
									}
								}
								currentFileStats.HandleLine(tmp.substring(0, i));
								output.setLength(0);
								output.append(tmp.substring(i));
							}
							currentFileStats.setWritingFunction(true);
							inFunction = true;
							functionBracketTracker++;
						}
						else
						{
							functionBracketTracker++;
						}
					}
					else if (c == '}')
					{
						functionBracketTracker--;
						if (functionBracketTracker == 0)
						{
							if (output.length() > 0)
							{
								currentFileStats.HandleLine(output.toString());
								output.setLength(0);
							}
							inFunction = false;
							currentFileStats.setWritingFunction(false);
							currentFileStats.FunctionDone();
						}
					}
				}
				else if (c == '#')
				{
					return;
				}
				else if (c == '<' && (currentCharIndex + 1) < line.length() && line.charAt(currentCharIndex + 1) == '<')
				{
					output.append('{');
					currentCharIndex += 2;
				}
				else if (c == '>' && (currentCharIndex + 1) < line.length() && line.charAt(currentCharIndex + 1) == '>')
				{
					output.append('}');
					currentCharIndex += 2;
				}
				else if (c == '"')
				{
					tempIndex = currentCharIndex + 1;
					while (line.charAt(tempIndex) != '"')
					{
						tempIndex++;
					}
					output.append("s");
					currentCharIndex = tempIndex + 1;
				}
				else if (c == '\'')
				{
					tempIndex = currentCharIndex + 1;
					while (line.charAt(tempIndex) != '\'')
					{
						tempIndex++;
					}
					output.append("h");
					currentCharIndex = tempIndex + 1;
				}
				else if (c == '/' && line.charAt(currentCharIndex + 1) == '/')
				{
					return;
				}
				else if (c == '/' && line.charAt(currentCharIndex + 1) == '*')
				{
					inCommentBlock = true;
					currentCharIndex += 2;
				}
				else
				{
					if (c == ';' && output.length() > 0 && output.charAt(output.length() - 1) == ';')
					{
						currentCharIndex++;
						break;
					}
					else if (c == ';' && output.length() == 0 && currentFileStats.GetLastAddedChar() == ';')
					{
						currentCharIndex++;
						break;
					}
					else if (c == ';' && output.length() == 0 && currentFileStats.GetLastAddedChar() == '}')
					{
						currentCharIndex++;
						break;
					}
					if (c == '(')
					{
						if (!inFunction)
						{
							if (output.length() == 0)
							{
								String str = "";
								if (currentFileStats.GetLastAddedChar() == 'v')
								{
									while (FileComparer.lcs(str, "v").length() == 0)
									{
										str = currentFileStats.ExtractLastOuterCodeLine() + str;
									}
									currentFileStats.HandleLine("f");
								}
							}
						}
						else
						{
							if (output.length() == 0)
							{
								String str = "";
								if (currentFileStats.GetLastAddedChar() == 'v')
								{
									while (FileComparer.lcs(str, "v").length() == 0)
									{
										str = currentFileStats.ExtractLastFunctionCodeLine() + str;
									}
									for (int i = str.length() - 1; i >= 0; i--)
									{
										if (str.charAt(i) == 'v')
										{
											str = str.substring(0, i) + 'f' + str.substring(i + 1);
										}
									}
									currentFileStats.HandleLine(str);
								}
							}
						}
					}
					output.append(c);
					currentCharIndex++;
				}
			}
			else
			{
				if (c == '*' && currentCharIndex + 1 < line.length() && line.charAt(currentCharIndex + 1) == '/')
				{
					inCommentBlock = false;
					currentCharIndex++;
				}
				currentCharIndex++;
			}
		}

	}

	public static void InitializeCPPLists()
	{
		String[] keywordList = new String[]
		{ "alignas", "alignof", "and", "and_eq", "asm", "atomic_cancel", "atomic_commit", "atomic_noexcept", "auto",
				"bitand", "bitor", "bool", "break", "case", "catch", "char", "char16_t", "char32_t", "class", "compl",
				"concept", "const", "constexpr", "const_cast", "continue", "co_await", "co_", "co_yield", "decltype",
				"default", "delete", "do", "double", "dynamic_cast", "else", "enum", "explicit", "export", "extern",
				"false", "float", "for", "friend", "goto", "if", "import", "inline", "int", "long", "module", "mutable",
				"namespace", "new", "noexcept", "not", "not_eq", "nullptr", "operator", "or", "or_eq", "out", "private",
				"protected", "public", "register", "reinterpret_cast", "requires", "return", "short", "signed",
				"sizeof", "static", "static_assert", "static_cast", "std", "string", "struct", "switch", "synchronized",
				"template", "this", "thread_local", "throw", "true", "try", "typedef", "typeid", "typename", "union",
				"unsigned", "using", "virtual", "void", "volatile", "wchar_t", "while", "xor", "xor_eq" };
		String[] typeList = new String[]
		{ "unsigned", "char", "string", "bool", "void", "short", "int", "long", "float", "double" };
		Collections.addAll(keywords, keywordList);
		Collections.addAll(types, typeList);
	}

	public static void InitializeCommonKeywordsMap()
	{
		commonKeywords = new HashMap<>();
		commonKeywords.put("do", 'o');
		commonKeywords.put("using", 'u');
		commonKeywords.put("namespace", 'm');
		commonKeywords.put("for", 'r');
		commonKeywords.put("switch", 'w');
		commonKeywords.put("case", 'c');
		commonKeywords.put("break", 'b');
		commonKeywords.put("if", 'x');
		commonKeywords.put("else", 'e');
		commonKeywords.put("false", 'l');
		commonKeywords.put("true", 'q');
		commonKeywords.put("while", 'y');
		commonKeywords.put("default", 'a');
		commonKeywords.put("return", 'z');
		commonKeywords.put("class", 'i');
	}

	private static boolean isType(String str)
	{
		for (String type : types)
		{
			if (str.equals(type))
			{
				return true;
			}
		}
		return false;
	}

	private static boolean isNumberType(String str)
	{
		String[] numTypes =
		{ "short", "int", "long", "float", "double" };
		for (String numType : numTypes)
		{
			if (numType.equals(str))
			{
				return true;
			}
		}
		return false;
	}

	private static boolean isKeyword(String str)
	{
		for (String keyword : keywords)
		{
			if (str.equals(keyword))
			{
				return true;
			}
		}
		return false;
	}

	private static char GetKeywordChar(String str)
	{
		return commonKeywords.getOrDefault(str, 'k');
	}
}