package com.softwareengineering.spamjam;

/**
 * Created by Deepak on 04-11-2017.
 */
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MessageCleaning {
    private char[] b;
    private int i,     /* offset into b */
            i_end, /* offset to end of stemmed word */
            j, k;
    private static final int INC = 50;
    /* unit of size whereby b is increased */
    public MessageCleaning()
    {
        b = new char[INC];
        i = 0;
        i_end = 0;
    }

    /**
     * Add a character to the word being stemmed.  When you are finished
     * adding characters, you can call stem(void) to stem the word.
     */

    public void add(char ch)
    {  if (i == b.length){

        char[] new_b = new char[i+INC];
        for (int c = 0; c < i; c++){
            new_b[c] = b[c];
        }
        b = new_b;
    }
        b[i++] = ch;
    }


    /** Adds wLen characters to the word being stemmed contained in a portion
     * of a char[] array. This is like repeated calls of add(char ch), but
     * faster.
     */

    public void add(char[] w, int wLen)
    {  if (i+wLen >= b.length){
        char[] new_b = new char[i+wLen+INC];

        for (int c = 0; c < i; c++){
            new_b[c] = b[c];
        }
        b = new_b;
    }

        for (int c = 0; c < wLen; c++){
            b[i++] = w[c];
        }
    }

    /**
     * After a word has been stemmed, it can be retrieved by toString(),
     * or a reference to the internal buffer can be retrieved by getResultBuffer
     * and getResultLength (which is generally more efficient.)
     */
    public String toString()
    {
        return new String(b,0,i_end);
    }

    /**
     * Returns the length of the word resulting from the stemming process.
     */
    public int getResultLength()
    {
        return i_end;
    }

    /**
     * Returns a reference to a character buffer containing the results of
     * the stemming process.  You also need to consult getResultLength()
     * to determine the length of the result.
     */
    public char[] getResultBuffer()
    {
        return b;
    }

	   /* cons(i) is true <=> b[i] is a consonant. */

    private final boolean cons(int i)
    {
        switch (b[i]){
            case 'a': case 'e': case 'i': case 'o': case 'u': return false;
            case 'y': return (i==0) ? true : !cons(i-1);
            default: return true;
        }
    }

	   /* m() measures the number of consonant sequences between 0 and j. if c is
	      a consonant sequence and v a vowel sequence, and <..> indicates arbitrary
	      presence,

	         <c><v>       gives 0
	         <c>vc<v>     gives 1
	         <c>vcvc<v>   gives 2
	         <c>vcvcvc<v> gives 3
	         ....
	   */

    private final int m()
    {  int n = 0;
        int i = 0;
        while(true){
            if (i > j) return n;
            if (! cons(i)) break; i++;
        }
        i++;

        while(true){
            while(true){
                if (i > j) return n;
                if (cons(i)) break;
                i++;
            }
            i++;
            n++;

            while(true){
                if (i > j) return n;
                if (! cons(i)) break;
                i++;
            }
            i++;
        }
    }

	   /* vowelinstem() is true <=> 0,...j contains a vowel */

    private final boolean vowelinstem()
    {
        int i;
        for (i = 0; i <= j; i++){
            if (! cons(i)){
                return true;
            }
        }

        return false;
    }

	   /* doublec(j) is true <=> j,(j-1) contain a double consonant. */

    private final boolean doublec(int j)
    {
        if (j < 1){
            return false;
        }
        if (b[j] != b[j-1]){
            return false;
        }

        return cons(j);
    }

	   /* cvc(i) is true <=> i-2,i-1,i has the form consonant - vowel - consonant
	      and also if the second c is not w,x or y. this is used when trying to
	      restore an e at the end of a short word. e.g.

	         cav(e), lov(e), hop(e), crim(e), but
	         snow, box, tray.

	   */

    private final boolean cvc(int i)
    {
        if (i < 2 || !cons(i) || cons(i-1) || !cons(i-2)){
            return false;
        }

        int ch = b[i];
        if (ch == 'w' || ch == 'x' || ch == 'y'){
            return false;
        }

        return true;
    }

    private final boolean ends(String s)
    {
        int l = s.length();
        int o = k-l+1;
        if (o < 0){
            return false;
        }

        for (int i = 0; i < l; i++){
            if (b[o+i] != s.charAt(i)){
                return false;
            }
        }
        j = k-l;
        return true;
    }

	   /* setto(s) sets (j+1),...k to the characters in the string s, readjusting
	      k. */

    private final void setto(String s)
    {
        int l = s.length();
        int o = j+1;
        for (int i = 0; i < l; i++){
            b[o+i] = s.charAt(i);
        }
        k = j+l;
    }

	   /* r(s) is used further down. */

    private final void r(String s)
    {
        if (m() > 0){
            setto(s);
        }
    }

	   /* step1() gets rid of plurals and -ed or -ing. e.g.

	          caresses  ->  caress
	          ponies    ->  poni
	          ties      ->  ti
	          caress    ->  caress
	          cats      ->  cat

	          feed      ->  feed
	          agreed    ->  agree
	          disabled  ->  disable

	          matting   ->  mat
	          mating    ->  mate
	          meeting   ->  meet
	          milling   ->  mill
	          messing   ->  mess

	          meetings  ->  meet

	   */

    private final void step1()
    {
        if (b[k] == 's'){
            if (ends("sses")){
                k -= 2;
            }
            else if (ends("ies")){
                setto("i");
            }
            else if (b[k-1] != 's'){
                k--;
            }
        }

        if (ends("eed")){
            if (m() > 0) k--;
        }
        else if ((ends("ed") || ends("ing")) && vowelinstem())
        {  k = j;
            if (ends("at")) setto("ate"); else
            if (ends("bl")) setto("ble"); else
            if (ends("iz")) setto("ize"); else
            if (doublec(k))
            {  k--;
                {  int ch = b[k];
                    if (ch == 'l' || ch == 's' || ch == 'z') k++;
                }
            }
            else if (m() == 1 && cvc(k)) setto("e");
        }
    }

	   /* step2() turns terminal y to i when there is another vowel in the stem. */

    private final void step2() { if (ends("y") && vowelinstem()) b[k] = 'i'; }

	   /* step3() maps double suffices to single ones. so -ization ( = -ize plus
	      -ation) maps to -ize etc. note that the string before the suffix must give
	      m() > 0. */

    private final void step3() { if (k == 0) return; /* For Bug 1 */ switch (b[k-1])
    {
        case 'a': if (ends("ational")) { r("ate"); break; }
            if (ends("tional")) { r("tion"); break; }
            break;
        case 'c': if (ends("enci")) { r("ence"); break; }
            if (ends("anci")) { r("ance"); break; }
            break;
        case 'e': if (ends("izer")) { r("ize"); break; }
            break;
        case 'l': if (ends("bli")) { r("ble"); break; }
            if (ends("alli")) { r("al"); break; }
            if (ends("entli")) { r("ent"); break; }
            if (ends("eli")) { r("e"); break; }
            if (ends("ousli")) { r("ous"); break; }
            break;
        case 'o': if (ends("ization")) { r("ize"); break; }
            if (ends("ation")) { r("ate"); break; }
            if (ends("ator")) { r("ate"); break; }
            break;
        case 's': if (ends("alism")) { r("al"); break; }
            if (ends("iveness")) { r("ive"); break; }
            if (ends("fulness")) { r("ful"); break; }
            if (ends("ousness")) { r("ous"); break; }
            break;
        case 't': if (ends("aliti")) { r("al"); break; }
            if (ends("iviti")) { r("ive"); break; }
            if (ends("biliti")) { r("ble"); break; }
            break;
        case 'g': if (ends("logi")) { r("log"); break; }
    } }

	   /* step4() deals with -ic-, -full, -ness etc. similar strategy to step3. */

    private final void step4() { switch (b[k])
    {
        case 'e': if (ends("icate")) { r("ic"); break; }
            if (ends("ative")) { r(""); break; }
            if (ends("alize")) { r("al"); break; }
            break;
        case 'i': if (ends("iciti")) { r("ic"); break; }
            break;
        case 'l': if (ends("ical")) { r("ic"); break; }
            if (ends("ful")) { r(""); break; }
            break;
        case 's': if (ends("ness")) { r(""); break; }
            break;
    } }

	   /* step5() takes off -ant, -ence etc., in context <c>vcvc<v>. */

    private final void step5()
    {   if (k == 0) return; /* for Bug 1 */ switch (b[k-1])
    {  case 'a': if (ends("al")) break; return;
        case 'c': if (ends("ance")) break;
            if (ends("ence")) break; return;
        case 'e': if (ends("er")) break; return;
        case 'i': if (ends("ic")) break; return;
        case 'l': if (ends("able")) break;
            if (ends("ible")) break; return;
        case 'n': if (ends("ant")) break;
            if (ends("ement")) break;
            if (ends("ment")) break;
	                    /* element etc. not stripped before the m */
            if (ends("ent")) break; return;
        case 'o': if (ends("ion") && j >= 0 && (b[j] == 's' || b[j] == 't')) break;
	                                    /* j >= 0 fixes Bug 2 */
            if (ends("ou")) break; return;
	                    /* takes care of -ous */
        case 's': if (ends("ism")) break; return;
        case 't': if (ends("ate")) break;
            if (ends("iti")) break; return;
        case 'u': if (ends("ous")) break; return;
        case 'v': if (ends("ive")) break; return;
        case 'z': if (ends("ize")) break; return;
        default: return;
    }
        if (m() > 1) k = j;
    }

	   /* step6() removes a final -e if m() > 1. */

    private final void step6()
    {  j = k;
        if (b[k] == 'e')
        {  int a = m();
            if (a > 1 || a == 1 && !cvc(k-1)) k--;
        }
        if (b[k] == 'l' && doublec(k) && m() > 1) k--;
    }

    /** Stem the word placed into the Stemmer buffer through calls to add().
     * Returns true if the stemming process resulted in a word different
     * from the input.  You can retrieve the result with
     * getResultLength()/getResultBuffer() or toString().
     */
    public void stem()
    {  k = i - 1;
        if (k > 1)
        { step1(); step2(); step3(); step4(); step5(); step6(); }
        i_end = k+1; i = 0;
    }

    public static String changeInBaseForm(String message)
    {
        String result = "";
        char[] w = new char[501];
        MessageCleaning s = new MessageCleaning();
        int index = 0;
        int len = message.length();

        char ch;
        while(index < len){
            ch = message.charAt(index++);

            if(Character.isLetter(ch)){
                int j = 0;

                while(true){
                    w[j] = ch;
                    if (j < 500) j++;

                    if(index < message.length()){
                        ch = message.charAt(index++);
                    }
                    else
                    {
                        ch = ' ';
                    }
                    if (!Character.isLetter((char) ch))
                    {

                        for (int c = 0; c < j; c++) s.add(w[c]);

                        s.stem();
                        {  String u;

                            u = s.toString();
                            if(u.length() > 0)
                                result += u+" ";
                            //System.out.print(u);
                        }
                        break;
                    }
                }
            }
            if (ch < 0) break;
        }
        return result.trim();
    }






    // function to preprocess on word

    // stop word hashset
    public static HashSet<String> stopWordsSet(){
        String stopWords[] = {"a", "able", "and", "about", "above", "according", "accordingly", "across", "actually", "after", "afterwards",
                "again", "against", "all", "allow", "allows", "almost", "alone", "along", "already", "also", "although", "always", "am", "among",
                "amongst", "an", "and", "another", "any", "anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart",
                "appear", "appreciate", "appropriate", "are", "around", "as", "aside", "ask", "asking", "associated", "at", "available", "away",
                "awfully", "b", "B", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being",
                "believe", "below", "beside", "besides", "best", "better", "between", "beyond", "both", "brief", "but", "by", "c", "came", "can",
                "cannot", "cant", "cause", "causes", "certain", "certainly", "changes", "clearly", "co", "com", "come", "comes", "concerning",
                "consequently", "consider", "considering", "contain", "containing", "contains", "corresponding", "could", "course", "currently",
                "d", "definitely", "described", "despite", "did", "different", "do", "does", "doing", "done", "down", "downwards", "during", "e",
                "each", "edu", "eg", "eight", "either", "else", "elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every",
                "everybody", "everyone", "everything", "everywhere", "ex", "exactly", "example", "except", "f", "far", "few", "fifth", "first", "five",
                "followed", "following", "follows", "for", "former", "formerly", "forth", "four", "from", "further", "furthermore", "g", "get", "gets",
                "getting", "given", "gives", "go", "goes", "going", "gone", "got", "gotten", "greetings", "h", "had", "happens", "hardly", "has", "have",
                "having", "he", "hello", "help", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "hi", "him",
                "himself", "his", "hither", "hopefully", "how", "howbeit", "however", "i", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc",
                "indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is", "it", "its", "itself", "j", "just",
                "k", "keep", "keeps", "kept", "know", "knows", "known", "l", "last", "lately", "later", "latter", "latterly", "least", "less", "lest",
                "let", "like", "liked", "likely", "little", "ll", "look", "looking", "looks", "ltd", "m", "mainly", "many", "may", "maybe", "me", "mean",
                "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must", "my", "myself", "n", "name", "namely", "nd", "near",
                "nearly", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor",
                "normally", "not", "nothing", "novel", "now", "nowhere", "o", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on", "once", "one",
                "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", "own",
                "p", "particular", "particularly", "per", "perhaps", "placed", "please", "plus", "possible", "presumably", "probably", "provides", "q", "que",
                "quite", "qv", "r", "rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively", "respectively", "right",
                "s", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self",
                "selves", "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "she", "should", "since", "six", "so", "some", "somebody",
                "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified", "specify", "specifying",
                "still", "sub", "such", "sup", "sure", "t", "take", "taken", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "thats", "the",
                "their", "theirs", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "theres", "thereupon",
                "these", "they", "think", "third", "this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "to",
                "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two", "u", "un", "under", "unfortunately",
                "unless", "unlikely", "until", "unto", "up", "upon", "us", "use", "used", "useful", "uses", "using", "usually", "uucp", "v", "value", "various",
                "ve", "very", "via", "viz", "vs", "w", "want", "wants", "was", "way", "we", "welcome", "well", "went", "were", "what", "whatever", "when", "whence",
                "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "with", "while",
                "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "wonder", "would", "would",
                "x", "y", "yes", "yet", "you", "your", "yours", "yourself", "yourselves", "z", "zero", "-", "_", "+", ".", "&", "|"};

       // String [] s1 = {"a","an","the","of","on","i","you","we","up","upom"};

        return new HashSet<String>(Arrays.asList(stopWords));
    }


    public static String wordCleaning(String input)
    {
        input = input.trim();
        String result = "";

        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(input);


        if(input.endsWith("?")||input.endsWith(".")||input.endsWith("!")||input.endsWith(":")||input.endsWith(",")||
                input.endsWith("'")||input.endsWith(")")){
            input = input.substring(0, input.length()-1);
        }

        if(input.startsWith("#")||input.startsWith("(")||input.startsWith(")")||input.startsWith("+")||input.startsWith(":")||
                input.startsWith("?")||input.startsWith("@")||input.startsWith("~")){
            input = input.substring(1, input.length());
        }

        if(input.startsWith("http://")||input.startsWith("https://")||input.startsWith("www.")){
            result = "weblink";
        }
        else if(matcher.matches()){
            result = "emailid";
        }

        else{
            int f1 = 0;
            int f2 = 0;
            int f3 = 0;
            for(int i = 0;i < input.length();i++){
                if((input.charAt(i) >= '0' && input.charAt(i) <= '9')){
                    f1 = 1;
                }
                else if((input.charAt(i)>='a' && input.charAt(i) <= 'z')||input.charAt(i)=='*'||input.charAt(i)=='#'
                        || input.charAt(i)== '-'||input.charAt(i)=='.'||input.charAt(i)==','||input.charAt(i)==')'||
                        input.charAt(i)=='('||input.charAt(i)==':'||input.charAt(i)=='%'||input.charAt(i)=='+'||
                        input.charAt(i)=='-'||input.charAt(i)=='_'||input.charAt(i)=='['||input.charAt(i)==']'||
                        input.charAt(i)=='{'||input.charAt(i)=='}'||input.charAt(i)=='@'||input.charAt(i)=='?'){
                    f2 = 1;
                }
                else{
                    f3 = 1;
                }
            }


            if(f1==1 && f2 == 0 && f3 == 0){
                result = "number";
            }
            else if(f1 == 1 && f2 == 1 && f3 == 0){
                result = "alphanumber";
            }
            else{
                result = input;
            }

        }
        return result;
    }

    public static String newWordCleaning(String message){
        String result = "";

        String previous = "";
        String pPrevious = "";
        HashSet<String> stopWords = stopWordsSet();

        int letter = 0;
        int number = 0;
        int dialer = 0;
        for(int i =0;i < message.length();i++){
            if(message.charAt(i)>='a' && message.charAt(i) <= 'z'){
                previous += message.charAt(i);
                letter = 1;
            }
            else if(message.charAt(i)>='0' && message.charAt(i) <= '9'){
                previous += message.charAt(i);
                number = 1;
            }
            else if(message.charAt(i)=='.' || message.charAt(i)=='#'||message.charAt(i)=='*')
            {
                previous += message.charAt(i);
                dialer = 1;
            }
            else{
                if(previous.length() > 1 && !stopWords.contains(previous)){
                    if(letter==1 && number == 0 && dialer==0){
                        result += previous+" ";
                        pPrevious = previous;
                    }
                    else if(letter==1 && number == 1 && dialer==0 && !pPrevious.equals("alphanumeric")){
                        result += "alphanumeric ";
                        pPrevious = "alphanumeric";
                    }
                    else if(letter == 0 && number == 1 && dialer == 0 && !pPrevious.equals("digit")){
                        result += "digit ";
                        pPrevious = "digit";

                    }
                    else if(letter == 0 && number == 1 && dialer == 1 && !pPrevious.equals("dialer")){
                        result += "dialer ";
                        pPrevious = "dialer";

                    }
                    else{
                        result += "typer ";
                        pPrevious = "typer";
                    }
                }
                letter = dialer = number = 0;
                previous = "";


            }

        }


        return result.trim();
    }



    // to clean the message
    public static String messageCleaning(String message)
    {
        String result = "";

        String [] spliter = message.toLowerCase().split("\\s+");

        HashSet<String> stopSet = stopWordsSet();

        for(int i = 0;i < spliter.length;i++)
        {
            if(!stopSet.contains(spliter[i].trim())){
                String cleanWord = wordCleaning(spliter[i]);
                if(cleanWord.length() > 2){
                    result += cleanWord+" ";
                }
            }
        }

        //System.out.println("result = "+result);
        //result = changeInBaseForm(result.toLowerCase());

        return result.trim();
    }

}
