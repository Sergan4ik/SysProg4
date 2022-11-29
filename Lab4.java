
import ProcessResult.ProcessResult;

import java.nio.file.*;
import java.io.*;

public class Lab4 {

    public static void main(String[] args) throws IOException {
        String filename = "source.js";

        String source = "";
        try {
            source = Files.readString(Path.of(filename));
        } catch (IOException e) {
            System.err.println(e);
        }

        DFA dfa = new DFA(source);
        dfa.processCode();
    }
}
