package com.example.ashishbahl.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ashish Bahl on 05-Jun-16.
 */
public class Movie implements Parcelable {
    String id;
    String posterpath;
    String title;
    String overview;
    String voteavg;
    String reldate;

    public Movie(String id,String posterpath, String title, String overview, String voteavg, String reldate){
        this.id = id;
        this.posterpath=posterpath;
        this.title=title;
        this.overview = overview;
        this.voteavg=voteavg;
        this.reldate=reldate;
    }
    public String getId(){
        return this.id;
    }
    public String getPoster(){
        return this.posterpath;
    }
    public String getTitle(){
        return this.title;
    }
    public String getSynopsis(){
        return this.overview;
    }
    public String getVoteavg(){
        return this.voteavg;
    }
    public String getReldate() {
        return this.reldate;
    }

    // Using the `in` variable, we can retrieve the values that
    // we originally wrote into the `Parcel`.  This constructor is usually
    // private so that only the `CREATOR` field can access.

    private Movie(Parcel in){
        id=in.readString();
        posterpath = in.readString();
        title = in.readString();
        overview=in.readString();
        voteavg=in.readString();
        reldate=in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // This is where you write the values you want to save to the `Parcel`.
    // The `Parcel` class has methods defined to help you save all of your values.
    // Note that there are only methods defined for simple values, lists, and other Parcelable objects.
    // You may need to make several classes Parcelable to send the data you want.
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(posterpath);
        parcel.writeString(title);
        parcel.writeString(overview);
        parcel.writeString(voteavg);
        parcel.writeString(reldate);
    }

    // After implementing the `Parcelable` interface, we need to create the
    // `Parcelable.Creator<MyParcelable> CREATOR` constant for our class;
    // Notice how it has our class specified as its type.

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        // We just need to copy this and change the type to match our class.

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
