//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    // An online text processing service needs a feature that compresses strings to save space
    // and reduce transmission time – you're the developer assigned to this task.
    // Implement a function that performs a basic string compression using the counts
    // of repeated characters. For example, the string "aabcccccaaa" would become "a2b1c5a3".

    public static String compressString(String input) {
        if(input.length() == 0 || input == null){
            return null;
        }

        StringBuilder result = new StringBuilder();
        char current = input.charAt(0);
        result.append(current);
        short counter = 0;

        for (int i = 0; i < input.length(); i++) {
            if(input.charAt(i) == current){
                counter++;
            } else {
                result.append(Integer.toString(counter));
                current = input.charAt(i);
                result.append(current);
                counter = 1;
            }
        }

        result.append(Integer.toString(counter));
        return result.toString();
    }

    // Create two versions of a function that, for a given integer N >= 0,
    // calculates a real number: the sum
    //     1 + 1/2 + 1/4 + 1/8 + ... + 1/2^N
    // One of the versions should use recursion, the other – iteration.

    public static double sumIter(int N){
        double result = 0;
        double currentPowerOfTwo = 0;

        for(int i = 0; i <= N; i++){
            result += 1/(Math.pow(2, currentPowerOfTwo));
            currentPowerOfTwo = currentPowerOfTwo + 1;
        }

        return result;
    }

    public static double sumRec(int N){
        if(N == 0){
            return 1;
        } else {
            return sumRec(N-1) + 1/(Math.pow(2, N));
        }
    }

    public static void main(String[] args) {
        System.out.println(compressString("aaaaaaaaaac"));
        System.out.println(sumIter(2));
        System.out.println(sumRec(3));
    }
}
