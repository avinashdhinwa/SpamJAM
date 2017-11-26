package com.softwareengineering.spamjam;

/**
 * Created by Deepak on 04-11-2017.
 */

import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MessageCleaning {

    // method to return list of stops word of english

    /**
     * @return hash table of stop words
     */
    public static HashSet<String> stopWordsSet() {
        // string array of stop words
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

        // convert list in array and return
        return new HashSet<String>(Arrays.asList(stopWords));
    }

    // method to clean the given word
    /**
     *
     * @param input message string
     * @return cleaned message
     */
    public static String wordCleaning(String input) {
        input = input.trim();
        String result = ""; // output string

        // check if current word is email id
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(input);

        // remove special character from last
        if (input.endsWith("?") || input.endsWith(".") || input.endsWith("!") || input.endsWith(":") || input.endsWith(",") ||
                input.endsWith("'") || input.endsWith(")")) {
            input = input.substring(0, input.length() - 1);
        }

        // remove special character from last
        if (input.startsWith("#") || input.startsWith("(") || input.startsWith(")") || input.startsWith("+") || input.startsWith(":") ||
                input.startsWith("?") || input.startsWith("@") || input.startsWith("~")) {
            input = input.substring(1, input.length());
        }

        // string start with http or https then convert it weblink
        if (input.startsWith("http://") || input.startsWith("https://") || input.startsWith("www.")) {
            result = "weblink";
        } else if (matcher.matches()) {
            result = "emailid";
        } else {
            int f1 = 0;
            int f2 = 0;
            int f3 = 0;
            for (int i = 0; i < input.length(); i++) {
                if ((input.charAt(i) >= '0' && input.charAt(i) <= '9')) {
                    f1 = 1;
                } else if ((input.charAt(i) >= 'a' && input.charAt(i) <= 'z') || input.charAt(i) == '*' || input.charAt(i) == '#'
                        || input.charAt(i) == '-' || input.charAt(i) == '.' || input.charAt(i) == ',' || input.charAt(i) == ')' ||
                        input.charAt(i) == '(' || input.charAt(i) == ':' || input.charAt(i) == '%' || input.charAt(i) == '+' ||
                        input.charAt(i) == '-' || input.charAt(i) == '_' || input.charAt(i) == '[' || input.charAt(i) == ']' ||
                        input.charAt(i) == '{' || input.charAt(i) == '}' || input.charAt(i) == '@' || input.charAt(i) == '?') {
                    f2 = 1;
                } else {
                    f3 = 1;
                }
            }

            // convert all number in number
            if (f1 == 1 && f2 == 0 && f3 == 0) {
                result = "number";
            } else if (f1 == 1 && f2 == 1 && f3 == 0) {
                result = "alphanumber"; // convert all alphanumber in same
            } else {
                result = input;
            }

        }
        return result;
    }

    // method to clean the message
    /**
     *
     * @param message => given message
     * @return => return clean message
     */
    public static String newWordCleaning(String message) {
        StringBuilder result = new StringBuilder();
        String previous = ""; // current word of message
        String pPrevious = ""; // previous word of message
        int letter = 0; // flag for a to z
        int number = 0; // flag for 0 to 9
        int dialer = 0; // flag for other
        for (int i = 0; i < message.length(); i++) {
            if (message.charAt(i) >= 'a' && message.charAt(i) <= 'z') {
                previous += message.charAt(i);
                letter = 1;
            } else if (message.charAt(i) >= '0' && message.charAt(i) <= '9') {
                previous += message.charAt(i);
                number = 1;
            } else if (message.charAt(i) == '.' || message.charAt(i) == '#' || message.charAt(i) == '*') {
                previous += message.charAt(i);
                dialer = 1;
            } else {
                if (previous.length() > 1) {
                    if (letter == 1 && number == 0 && dialer == 0) {
                        result.append(previous + " "); // add same if only a to z
                        pPrevious = previous;
                    } else if (letter == 1 && number == 1 && dialer == 0 && !pPrevious.equals("alphanumeric")) {
                        result.append("alphanumeric "); // add alphanumeric for alpha numeric
                        pPrevious = "alphanumeric";
                    } else if (letter == 0 && number == 1 && dialer == 0 && !pPrevious.equals("digit")) {
                        result.append("digit "); // add digit for all digits
                        pPrevious = "digit";

                    } else if (letter == 0 && number == 1 && dialer == 1 && !pPrevious.equals("dialer")) {
                        result.append("dialer "); // add dialer for number and some special character
                        pPrevious = "dialer";

                    } else {
                        result.append("typer "); // add typer for all others
                        pPrevious = "typer";
                    }
                }
                letter = dialer = number = 0;
                previous = "";


            }

        }
        // do same outside the for loop
        if (previous.length() > 1) {
            if (letter == 1 && number == 0 && dialer == 0) {
                result.append(previous + " ");
            } else if (letter == 1 && number == 1 && dialer == 0 && !pPrevious.equals("alphanumeric")) {
                result.append("alphanumeric ");
            } else if (letter == 0 && number == 1 && dialer == 0 && !pPrevious.equals("digit")) {
                result.append("digit ");

            } else if (letter == 0 && number == 1 && dialer == 1 && !pPrevious.equals("dialer")) {
                result.append("dialer ");

            } else {
                result.append("typer ");
            }
        }


        return result.toString().trim();
    }

    // method to clean the message
    /**
     *
     * @param message => given message
     * @return => return clean message
     */
    public static String messageCleaning(String message) {
        String result = ""; // output string
        String[] spliter = message.toLowerCase().split("\\s+"); // split the string based on the white spaces

        HashSet<String> stopSet = stopWordsSet(); // hash table of stop words

        for (int i = 0; i < spliter.length; i++) {
            if (!stopSet.contains(spliter[i].trim())) {
                String cleanWord = wordCleaning(spliter[i]);
                if (cleanWord.length() > 2) {
                    result += cleanWord + " "; // add cleanWOrd only if length is grater than 2
                }
            }
        }
        return result.trim(); // return output
    }


    // method to clean the hindi message written in hindi

    /**
     *
     * @param message given hindi message
     * @return cleaned message
     */
    public static String HindiMessageCleaning(String message) {
        StringBuilder result = new StringBuilder(); // output string
        String current = ""; // current string
        int engFlag = 0; // Flag for english
        int hindiFlag = 0; // flag for hindi
        int numFlag = 0; // flag for number
        String previous = ""; // previous string

        for (int i = 0; i < message.length(); i++) {
            if (message.charAt(i) >= 2304 && message.charAt(i) < 2431) {
                current += message.charAt(i); // hindi flags
                hindiFlag = 1;
            } else if (message.charAt(i) >= 'a' && message.charAt(i) <= 'z') {
                current += message.charAt(i); // english flags
                engFlag = 1;

            } else if ((message.charAt(i) >= '0' && message.charAt(i) <= '9') || message.charAt(i) == '#' ||
                    message.charAt(i) == '+' || message.charAt(i) == '.' || message.charAt(i) == '%' || message.charAt(i) == '*') {
                current += message.charAt(i);
                numFlag = 1;

            } else if (current.length() > 0) {
                if (hindiFlag == 1 && engFlag == 0 && numFlag == 0) {
                    result.append(current + " "); // add directly if only hindi string
                    previous = current;
                } else if (hindiFlag == 0 && engFlag == 1 && numFlag == 0 && !previous.equals("english")) {
                    result.append("english "); // add english for all english words
                    previous = "english";
                } else if (hindiFlag == 0 && engFlag == 0 && numFlag == 1 && !previous.equals("number")) {
                    result.append("number "); // add number for all 0-9 words
                    previous = "number";

                } else if (hindiFlag == 0 && engFlag == 1 && numFlag == 1 && !previous.equals("alphaNumber")) {
                    result.append("alphaNumber "); // add alphaNumber for all alpha number
                    previous = "alphaNumber";

                } else if (hindiFlag == 1 && engFlag == 1 && !previous.equals("mix")) {
                    result.append("mix "); // add mix if all present
                    previous = "mix";

                } else if (hindiFlag == 1 && engFlag == 0 && numFlag == 1 && !previous.equals("hindiWithNumber")) {
                    result.append("hindiWithNumber "); // add hindiWithNumber for all number with hindi words
                    previous = "hindiWithNumber";

                }
                current = "";
                hindiFlag = 0;
                engFlag = 0;
                numFlag = 0;
            }


        }

        // do something for outer the loop
        if (current.length() > 0) {
            if (hindiFlag == 1 && engFlag == 0 && numFlag == 0) {
                result.append(current + " ");
            } else if (hindiFlag == 0 && engFlag == 1 && numFlag == 0 && !previous.equals("english")) {
                result.append("english ");
            } else if (hindiFlag == 0 && engFlag == 0 && numFlag == 1 && !previous.equals("number")) {
                result.append("number ");

            } else if (hindiFlag == 0 && engFlag == 1 && numFlag == 1 && !previous.equals("alphaNumber")) {
                result.append("alphaNumber ");

            } else if (hindiFlag == 1 && engFlag == 1 && !previous.equals("mix")) {
                result.append("mix ");

            } else if (hindiFlag == 1 && engFlag == 0 && numFlag == 1 && !previous.equals("hindiWithNumber")) {
                result.append("hindiWithNumber ");
            }
        }

        System.out.println("changed: " + result.toString().trim());
        return result.toString().trim();

    }

}
