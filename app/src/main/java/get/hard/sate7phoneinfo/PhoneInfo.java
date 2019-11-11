package get.hard.sate7phoneinfo;

import java.util.ArrayList;

public class PhoneInfo {
    private String mTitle;
    private String mContent;
    private PhoneInfoType mType;
    private ArrayList<PhoneInfo> mInfoCollection = new ArrayList<>();

    enum PhoneInfoType {
        SIMPLE,
        COLLECTION;
    }

    public PhoneInfo(String mTitle, String mContent) {
        this.mTitle = mTitle;
        this.mContent = mContent;
    }

    public void setInfoCollection(ArrayList<PhoneInfo> infoList) {
        mInfoCollection.clear();
        mInfoCollection.addAll(infoList);
    }

    public ArrayList<PhoneInfo> getInfoCollection() {
        return mInfoCollection;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getContent() {
        return mContent;
    }

    public PhoneInfoType getType() {
        return mType;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setContent(String mContent) {
        this.mContent = mContent;
    }

    public void setType(PhoneInfoType mType) {
        this.mType = mType;
    }

    @Override
    public String toString() {
        return "title:" + mTitle + ",content:" + mContent;
    }
}
