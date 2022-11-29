package ProcessResult;

public class ProcessResult {
    public int position;
    public String buffer;

    public ProcessResult(int charPosition, String buffer) {
        position = charPosition;
        this.buffer = buffer;
    }
}
