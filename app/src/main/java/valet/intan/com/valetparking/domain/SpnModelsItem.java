package valet.intan.com.valetparking.domain;

public class SpnModelsItem {
    private String mModelName = "";
    private int mModelConstant = 0;

    public SpnModelsItem(String modelName, int modelConstant) {
        mModelName = modelName;
        mModelConstant = modelConstant;
    }

    public int getModelConstant() {
        return mModelConstant;
    }

    @Override
    public String toString() {
        return mModelName;
    }
}
