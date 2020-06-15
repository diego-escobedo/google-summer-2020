// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;


public final class FindMeetingQuery {
public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        ArrayList<TimeRange> ret = new ArrayList<TimeRange>();
        int duration = ((Long)request.getDuration()).intValue();
        if (duration > 24*60) {
                return new ArrayList<TimeRange>();
        }
        for (int startMinute = 0; startMinute < 24*60-duration; startMinute++) {
                TimeRange proposedTime = TimeRange.fromStartDuration(startMinute, duration);
                boolean overlaps = false;
                for (Event event : events) {
                        boolean condition1 = proposedTime.overlaps(event.getWhen());
                        boolean condition2 = (!Collections.disjoint(request.getAttendees(), event.getAttendees()));
                        if (condition1 && condition2) {
                                overlaps = true;
                                break;
                        }
                }
                if (!overlaps) {
                        ret.add(proposedTime);
                }
        }
        if (ret.size() == 0) {
                return ret;
        }
        else {
                return process(ret);
        }
}

private Collection<TimeRange> process(ArrayList<TimeRange> unmergedRanges) {
        ArrayList<TimeRange> condensed = new ArrayList<TimeRange>();
        TimeRange first_one = unmergedRanges.get(0);
        unmergedRanges.remove(0);
        condensed.add(first_one);
        int counter = 0;
        for (TimeRange tr : unmergedRanges) {
                counter++;
                TimeRange potentialMerger = condensed.get(condensed.size()-1);
                condensed.remove(condensed.size()-1);
                if (tr.overlaps(potentialMerger)) {
                        if (counter == unmergedRanges.size()) {
                                TimeRange merge = TimeRange.fromStartEnd(potentialMerger.start(), tr.end(), true);
                                condensed.add(merge);
                        }
                        else {
                                TimeRange merge = TimeRange.fromStartEnd(potentialMerger.start(), tr.end(), false);
                                condensed.add(merge);
                        }

                }
                else {
                        condensed.add(potentialMerger);
                        condensed.add(tr);
                }
        }
        return condensed;
}
}
