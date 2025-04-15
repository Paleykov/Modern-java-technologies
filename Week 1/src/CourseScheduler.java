public class CourseScheduler {

    public static int maxNonOverlappingCourses(int[][] courses) {
        if (courses.length == 0)
            return 0;

        for (int i = 0; i < courses.length - 1; i++) {
            int minIndex = i;

            for (int j = i + 1; j < courses.length; j++) {
                if (courses[j][1] < courses[minIndex][1]) {
                    minIndex = j;
                }
            }

            int[] temp = courses[i];
            courses[i] = courses[minIndex];
            courses[minIndex] = temp;
        }

        int count = 0;
        int lastEndTime = Integer.MIN_VALUE;

        for (int i = 0; i < courses.length; i++) {
            if (courses[i][0] >= lastEndTime) {
                count++;
                lastEndTime = courses[i][1];
            }
        }
        return count;
    }
}
