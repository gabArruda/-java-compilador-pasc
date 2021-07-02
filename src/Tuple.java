import java.util.Objects;

public class Tuple {
  private final String naoTerminal;
  private final String terminal;

  public Tuple(String naoTerminal, String terminal) {
    this.naoTerminal = naoTerminal;
    this.terminal = terminal;
  }

  public String getTerminal() {
    return terminal;
  }

  public String getNaoTerminal() {
    return naoTerminal;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof Tuple))
      return false;
    Tuple tuple = (Tuple) o;
    return terminal.equals(tuple.terminal) && naoTerminal.equals(tuple.naoTerminal);
  }

  @Override
  public int hashCode() {
    return Objects.hash(naoTerminal, terminal);
  }
}
