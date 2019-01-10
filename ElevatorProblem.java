import java.io.File;
import java.net.URL;
import java.util.*;

public class ElevatorProblem {

    public static class ElevatorRequest {
        public long ms;
        public int floor;
        public int destination;

        public String toString() {
            return String.format("%d\t%d\t%d", ms, floor, destination);
        }
    }

    public static void main(String[] args) throws Exception {

        URL url = ElevatorProblem.class.getResource("input.txt");
        Scanner sc = new Scanner(new File(url.getPath()));

        String line = sc.nextLine();

        String[] tokens = line.split("[\\s|\\t]+");

        int n = Integer.parseInt(tokens[0]);

        int startingFloor = Integer.parseInt(tokens[1]);

        System.out.println("number of requests : " + n);
        System.out.println("starting floor : " + startingFloor);

        Queue<ElevatorRequest> q = new LinkedList<>();

        for (int i = 0; i < n; i++) {




            line = sc.nextLine();
            tokens = line.split("[\\s|\\t]+");

            ElevatorRequest req = new ElevatorRequest();
            req.ms = Long.parseLong(tokens[0]) * 1000;
            req.floor = Integer.parseInt(tokens[1]);
            req.destination = Integer.parseInt(tokens[2]);

            System.out.println(req);

            q.add(req);

        }

        Set<Integer> destSet = new TreeSet();

//        Runnable r = () -> {
//            try {
//                while(true) {
//                    System.out.println("destSet : " + destSet);
//                    Thread.sleep(1000);
//                }
//            } catch (InterruptedException iex) {
//
//            }
//        };
//
//        r.run();


        Thread foo = new Thread(() -> {
            while (true) {
                System.out.println("destSet : " + destSet);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException iex) {
                    System.out.println("foo interrupted : " + iex);
                }
            }
        });
        foo.start();

        // elevator thread
        Thread lt = new Thread(() -> {

            long start = System.currentTimeMillis();

            //destSet = new TreeSet();

            Integer currentFloor = startingFloor;

            Integer dest = null;

            Map<Integer, Set<Integer>> m = new HashMap();

            while (true) {

                long now = System.currentTimeMillis();

                long sinceStart = now - start;

                ElevatorRequest req = q.peek();

                if (req == null) {
                }
                else if (sinceStart >= req.ms) {
                    q.poll();

                    int reqOriginFloor = req.floor;

                    Set<Integer> set = m.get(reqOriginFloor);
                    if (set == null) set = new HashSet();
                    set.add(req.destination);
                    m.put(reqOriginFloor, set);

                    destSet.add(reqOriginFloor);
                }

                if (destSet.contains(currentFloor)) {
                    destSet.remove(currentFloor);
                    Set<Integer> currentFloorDestSet = m.get(currentFloor);
                    if (currentFloorDestSet != null) {
                        destSet.addAll(m.get(currentFloor));
                    }
                    m.remove(currentFloor);
                }

                Integer shortestDistance = Integer.MAX_VALUE;
                Integer closestFloor = null;

                for (Integer floor : destSet) {
                    Integer distance = Math.abs(currentFloor - floor);
                    if (distance < shortestDistance) {
                        shortestDistance = distance;
                        closestFloor = floor;
                    }
                }

                if (closestFloor == null) {
                }
                else if (closestFloor > currentFloor) {
                    currentFloor += 1;
                } else if (closestFloor < currentFloor) {
                    currentFloor -= 1;
                }

                if (q.isEmpty() && destSet.isEmpty()) {
                    System.exit(0);
                }

                try {
                    System.out.println(String.format("on floor %d", currentFloor));
                    Thread.sleep(1000);
                } catch (InterruptedException iex) {
                    System.err.println("Thread crashed : " + iex);
                }
            }

        });
        System.out.println("starting lt thread...");
        lt.start();
        System.out.println("started lt thread");

    }
}

