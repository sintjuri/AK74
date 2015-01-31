package com.onettm.ak74;

public enum PartEnum {

    First(R.drawable.part1, "first_part", R.drawable.ak74base_1, 0),
    Second(R.drawable.part2, "second_part", R.drawable.ak74base_2, 1),
    Third(R.drawable.part3, "third_part", R.drawable.ak74base_3, 2),
    Fourth(R.drawable.part4, "fourth_part", R.drawable.ak74base_4, 3),
    Fifth(R.drawable.part5, "fifth_part", R.drawable.ak74base_5, 4),
    Sixth(R.drawable.part6, "sixth_part", R.drawable.ak74base_6_full, 5);

    private final int resource;
    private final int akWithThisPartResource;
    private final int order;
    private final String tag;

    PartEnum(int resource, String tag, int akWithThisPartResource, int order){
        this.resource = resource;
        this.akWithThisPartResource = akWithThisPartResource;
        this.order = order;
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public int getResource() {
        return resource;
    }

    public int getAkWithThisPartResource() {
        return akWithThisPartResource;
    }

    public int getOrder() {
        return order;
    }

    public static PartEnum getPartByTag(String tag){
        for(PartEnum part : PartEnum.values()){
            if(part.getTag().equals(tag)){
                return part;
            }
        }
        return null;
    }

}

