package its.app.spat.sender.utils;

public class Reception {

    private char[] pkt;
    private boolean flag = false;

    public Reception(){        
    }

    public synchronized char[] get() throws InterruptedException {

        if (flag == false) {

            this.notify();
            
            this.wait(10000);
          
            if (flag == true) {

                flag = false;
                this.notify();

                return (this.pkt);

            } else {

               // System.out.println(" No response!!");
                return null;

            }

        } else {

            flag = false;
            this.notify();
            return (this.pkt);

        }
        

    }

    public synchronized void put(char[] pkt) {

        this.pkt = pkt;
        flag = true;
        this.notify();

    }

    public synchronized void flush() {
        flag = false;
    }
}