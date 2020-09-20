package com.conference.management;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

public class Conference {
    List<Talks> trackTalks;

    int totalTrackMinutes;
    int countTrack;
    int countTalks;

    // getter and setters
    public int getTotalTrackMinutes() {
        return totalTrackMinutes;
    }

    public void setTotalTrackMinutes(int totalTrackMinutes) {
        this.totalTrackMinutes = totalTrackMinutes;
    }

    public int getCountTrack() {
        return countTrack;
    }

    public void setCountTrack(int countTrack) {
        this.countTrack = countTrack;
    }

    public int getCountTalks() {
        return countTalks;
    }

    public void setCountTalks(int countTalks) {
        this.countTalks = countTalks;
    }

    public List<Talks> getTrackTalks() {
        return trackTalks;
    }
    public void setTrackTalks(List<Talks> trackTalks) {
        this.trackTalks = trackTalks;
    }

    // Methods

    public void ProcessTalksInput(String fileName){
        int id =0;
        int noOfTracks = 0;
        FileInputStream fstream = null;

        // Open the TestInputFile
        try {
            fstream = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;

        int intMinutes;
        int totalMinutes = 0;

        System.out.println("Test Input :");
        System.out.println("");

        //Read Input File Line By Line
        try {
            while ((strLine = br.readLine()) != null) {
                // if (lines start from // then ignore it) else get it.
                if(strLine.contains("//") || strLine.isEmpty())
                    continue;

                id = id +1 ;

                System.out.println(strLine);

                // Process the received line, extract title, minutes and if it has lightning instead of minutes value
                // the convert into 5 mins

                String Title = strLine.substring(0, strLine.lastIndexOf(" "));
                String MinutesString = strLine.substring(strLine.lastIndexOf(" ") + 1);
                String Minutes = strLine.replaceAll("\\D+", "");
                if("lightning".equals(MinutesString))
                {
                    intMinutes = 5;

                    totalMinutes = totalMinutes + intMinutes;
                }else
                {
                    intMinutes = Integer.parseInt(Minutes);
                    totalMinutes = totalMinutes + intMinutes;
                }

                // Create a Talk Object, Fill all the input values
                Talks singleTalk = new Talks(intMinutes,Title,id);

                // Add this Talk Object to the List of Track Talks
                trackTalks.add(singleTalk);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set the total no. of count talks.
        this.setCountTalks(id);

        // set total no. of minutes of talks.
        this.setTotalTrackMinutes(totalMinutes);

        // Calculate the no. of tracks
        Double totalMinutesInDouble =  totalMinutes*1.0;

        Double numberOfTracks =  totalMinutesInDouble/TrackConfiguration.TOTAL_CONFERENCE_TALKS_TRACK_MINUTES;

        double fractionalPart = numberOfTracks % 1;
        double integralPart = numberOfTracks - fractionalPart;

        int leftMinutes = totalMinutes - (int)integralPart*TrackConfiguration.TOTAL_CONFERENCE_TALKS_TRACK_MINUTES.intValue();

        // if it comes 1.5 or 1.4 or 1.8 - it will give the value of 2 Tracks
        if (leftMinutes > 0) {
            noOfTracks = (int) integralPart + 1;
        }else
        {
            noOfTracks = (int) integralPart;
        }

        this.setCountTrack(noOfTracks);

        // Sort all talks based on the talks-time in descending order.
        Collections.sort(trackTalks, new TalksCompare());

        //Close the input stream
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Just to show the empty lines to display the test-input
        System.out.println("");
        System.out.println("");
    }

   
    public int ScheduleTalksIntoTracks(int trackCountIndex, List<Talks> trackTalks, int trackCount,int startTalkIndex , int totalTalkCount){

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a");
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR, 9);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.AM_PM, Calendar.AM);

        int sum180 = TrackConfiguration.MORNING_TIME_MINUTES;
        int sum240 = TrackConfiguration.AFTERNOON_TIME_MINUTES;

        int TalkIndex;

        String sessionTime;
        String SessionTitle;

        for(TalkIndex=startTalkIndex; TalkIndex< totalTalkCount;TalkIndex++) {


            // Get the combination of 180 and fill it
            if (sum180 >= trackTalks.get(TalkIndex).getMinutes()) {
                sum180 = sum180 - trackTalks.get(TalkIndex).getMinutes();
                sessionTime = sdf.format(cal.getTime()) + " " + trackTalks.get(TalkIndex).getTitle() + " " + trackTalks.get(TalkIndex).getMinutes() + "min";
                trackTalks.get(TalkIndex).setTitle(sessionTime);
                cal.add(Calendar.MINUTE, trackTalks.get(TalkIndex).getMinutes());
                SessionTitle = "Track" + " " + (trackCountIndex + 1);
                trackTalks.get(TalkIndex).setTrackTitle(SessionTitle);
            }
            if (sum180 < trackTalks.get(TalkIndex).getMinutes())
                break;

            if (sum180 > 0)
                continue;

            if (sum180 <= 0)
                break;
        }

        trackTalks.get(TalkIndex).setLunchFlag(true);
        sessionTime = "12:00 PM" + " " + "Lunch";
        trackTalks.get(TalkIndex).setLunchTitle(sessionTime);
        cal.add(Calendar.MINUTE, 60);

        TalkIndex++;

        for(;TalkIndex< totalTalkCount;TalkIndex++) {
            // Get the combination of 180 and fill it
            if (sum240 >= trackTalks.get(TalkIndex).getMinutes()) {
                sum240 = sum240 - trackTalks.get(TalkIndex).getMinutes();
                sessionTime = sdf.format(cal.getTime()) + " " + trackTalks.get(TalkIndex).getTitle() + " " + trackTalks.get(TalkIndex).getMinutes() + "min";
                trackTalks.get(TalkIndex).setTitle(sessionTime);
                cal.add(Calendar.MINUTE, trackTalks.get(TalkIndex).getMinutes());
                SessionTitle = "Track" + " " + (trackCountIndex + 1);
                trackTalks.get(TalkIndex).setTrackTitle(SessionTitle);
            }
            if (sum240 < trackTalks.get(TalkIndex).getMinutes())
                break;

            if (sum240 > 0)
                continue;

            if (sum240 <= 0)
                break;
        }

        if(totalTalkCount == (TalkIndex))
            --TalkIndex;
        trackTalks.get(TalkIndex).setNetworkingFlag(true);
        sessionTime = "5:00 PM" + " " + "Networking Event";
        trackTalks.get(TalkIndex).setNetworkingTitle(sessionTime);

        // TBD : Add rules to re-evaluate the Schedule of Talks into Tracks e.g. If on evaluation its found that on Track-1
        // we have 30 free minutes and on Track-2 we have 45 free minutes, and one talk of 60 minutes need to schedule.
        // We can shuffle 30 mins talks from Track-1 to Track-2 , and accommodate this 60 mins talk to track-1
        // Only varieties of input will provide right sense.

        TalkIndex++;
        return TalkIndex;

    }

    public void OutputOfTalksIntoTracks(List<Talks> trackTalks){

        System.out.println("Test Output :");
        System.out.println("");
        String TrackTitle = "dummyValue";

        // Output the talks into tracks based on the totalTalks and the count of Talks.
        for(int trackCountIndex=0;trackCountIndex<trackTalks.size();trackCountIndex++)
        {

            // Print the Track Title
            if(!TrackTitle.equals(trackTalks.get(trackCountIndex).getTrackTitle()))
            {
                System.out.println(trackTalks.get(trackCountIndex).getTrackTitle() + ":");
                System.out.println("");
                TrackTitle = trackTalks.get(trackCountIndex).getTrackTitle();
            }

            // Print the prepared talk's title for this Track
            System.out.println(trackTalks.get(trackCountIndex).getTitle());

            // if lunch flag set then output the Lunch Title
            if(trackTalks.get(trackCountIndex).isLunchFlag())
            {
                System.out.println(trackTalks.get(trackCountIndex).getLunchTitle());
            }

            // if networking flag set then output the Networking Title
            if(trackTalks.get(trackCountIndex).isNetworkingFlag())
            {
                System.out.println(trackTalks.get(trackCountIndex).getNetworkingTitle());
                // simple convention to display extra lines.
                System.out.println("");
                System.out.println("");
            }

        }
    }

    // constructor
    Conference(){
        this.trackTalks = new ArrayList<Talks>();
    }
}


