package grammar;

import java.lang.Character;

public class Grammar {

    public boolean isPunctuation(char c) {
        return !(Character.isLetterOrDigit(c) || Character.isWhitespace(c));
    }


}
