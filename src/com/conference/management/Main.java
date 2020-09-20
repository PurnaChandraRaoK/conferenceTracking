package com.conference.management;

public class Main {

    public static void main(String[] args) {

       // Create a new Conference Object.
        Conference conference = new Conference();

        // Process the received input of Talks's Title and their time.
        conference.ProcessTalksInput(TrackConfiguration.TALKS_INPUT_FILE);

        // Get the no. of Tracks required to schedule all of these talks.
        int numberOfTracks = conference.getCountTrack();


        int startTalkIndex = 0;

        // Schedule the Talks into Tracks.
        for(int trackCount = 0; trackCount <numberOfTracks; trackCount++)
        {
            startTalkIndex = conference.ScheduleTalksIntoTracks(trackCount, conference.getTrackTalks(), conference.getCountTrack(), startTalkIndex, conference.getCountTalks());
        }

        // Output the schedule of Talks into tracks.
        conference.OutputOfTalksIntoTracks(conference.getTrackTalks());

    }
}
