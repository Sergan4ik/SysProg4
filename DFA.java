import ProcessResult.ProcessResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DFA {
    private String buffer = "";
    private State state = State.None;
    private boolean decimalPoint = false;
    private boolean decimalExponent = false;
    private String code;
    public DFA(String source) throws IOException {
        ResetOutput();
        code = source;
    }

    public void processCode() throws IOException {
        int i = 0;
        while (state != State.End) {
            if (i >= code.length()) {
                if (state == State.None) {
                    state = State.End;
                    System.out.println("\nParsed successfully");
                } else {
                    state = State.Error;
                    System.err.println("\nUnexpected end of file");
                }
                break;
            }

            char symbol = code.charAt(i);
            String operator;
            var res = ProcessState(symbol , code, i);
            if (res != null){
                i = res.position;
                buffer = res.buffer;
            }
        }
    }

    public ProcessResult ProcessState(char symbol, String text, int charPosition) throws IOException {
        String operator;
        switch (state) {
            case None:
                buffer = "";
                charPosition = ProcessNone(symbol, text, charPosition) + 1;
                break;
            case Identifier:
                charPosition = ProcessIdentifier(symbol, charPosition);
                break;
            case StringLiteral:
                charPosition = ProcessStringLiteral(symbol, charPosition);
                break;
            case NumberLiteral:
                charPosition = ProcessNumberLiteral(symbol, charPosition);
            case Comment:
                charPosition = ProcessComment(symbol, charPosition);
                break;
            case Error:
                System.err.println("\nInvalid token");
                return null;
            case End:
                return null;
        }
        return new ProcessResult(charPosition , buffer);
    }

    private int ProcessComment(char symbol, int charPosition) {
        if (symbol != '\n') {
            buffer += symbol;
        } else {
            state = State.None;
        }
        charPosition++;
        return charPosition;
    }

    private int ProcessNumberLiteral(char symbol, int charPosition) throws IOException {
        if (Character.isDigit(symbol)) {
            buffer += symbol;
            charPosition++;
        } else if (symbol == '.') {
            if (decimalPoint) {
                state = State.Error;
            } else {
                decimalPoint = true;
                buffer += symbol;
                charPosition++;
            }
        } else if ("eE".contains(""+ symbol)) {
            if (decimalExponent) {
                state = State.Error;
            } else {
                decimalExponent = true;
                buffer += symbol;
                charPosition++;
            }
        }
        if ("+-".contains(""+ symbol)) {
        char last = buffer.charAt(buffer.length()-1);
        if ("eE".contains(""+last)) {
            buffer += symbol;
            charPosition++;
        } else if (last == '.') {
            state = State.Error;
        } else {
            state = State.None;
        }
    } else {
        try {
            Double.parseDouble(buffer);
            log(buffer, "Number");
            state = State.None;
        } catch (NumberFormatException e) {
            state = State.Error;
        }
    }
        return charPosition;
    }

    private int ProcessStringLiteral(char symbol, int charPosition) throws IOException {
        buffer += symbol;
        if ("\"'`".contains(""+ symbol)) {
            log(buffer, "String");
            state = State.None;
        }
        charPosition++;
        return charPosition;
    }

    private int ProcessIdentifier(char symbol, int charPosition) throws IOException {
        if (Character.isLetter(symbol) || Character.isDigit(symbol) || "$_".contains(""+ symbol)) {
            buffer += symbol;
            charPosition++;
        } else {
            if (contains(JSSyntax.KEYWORDS, buffer)) {
                // found keyword
                log(buffer, "Keyword");
            } else {
                log(buffer, "Identifier");
            }
            state = State.None;
        }
        return charPosition;
    }

    private int ProcessNone(char symbol, String text, int charPosition) throws IOException {
        String operator;
        if (Character.isWhitespace(symbol)) {
            // found whitespace
        } else if (JSSyntax.DELIMITERS.contains(""+ symbol)) {
            // found delimiter
            log(""+ symbol, "Delimiters");
        } else if (symbol == '/' && text.length() > charPosition +1 && text.charAt(charPosition +1) == '/') {
            // found comment
            state = State.Comment;
            buffer += "//";
            charPosition++;
        } else if ((operator = startsWithOneOf(text.substring(charPosition), JSSyntax.OPERATORS)) != null) {
            // found operator
            log(operator, "Operator");
            charPosition += operator.length() - 1;
        } else if ("$_".contains(""+ symbol) || Character.isLetter(symbol)) {
            // found indentifier
            state = State.Identifier;
            buffer += symbol;
        } else if ("\"'`".contains(""+ symbol)) {
            // found string
            state = State.StringLiteral;
            buffer += symbol;
        } else if (Character.isDigit(symbol)) {
            // found number
            state = State.NumberLiteral;
            buffer += symbol;
        } else {
            state = State.Error;
            buffer += symbol;
        }
        return charPosition;
    }

    private boolean contains(String[] strings, String toFind) {
        for (String hay: strings) {
            if (hay.equals(toFind)) return true;
        }
        return false;
    }

    private String startsWithOneOf(String text, String[] words) {
        for (String word: words) {
            if (text.startsWith(word)) return word;
        }
        return null;
    }
    private void log(String token, String type , boolean writeToFile) throws IOException {
        System.out.println(token + " - " + type);
        if (writeToFile)
            logFile(token , type);
    }

    private void log(String token, String type) throws IOException {
        log(token , type , true);
    }
    private void logFile(String token, String type) throws IOException {
        String fileName = "result.txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName , true));
        writer.write(token + " - " + type);
        writer.newLine();
        writer.close();
    }
    private static void ResetOutput() throws IOException {
        String fileName = "result.txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName , false));
        writer.close();
    }
}
