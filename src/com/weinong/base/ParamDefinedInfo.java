package com.weinong.base;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

class ParamDefinedInfo {

	private String name;
	private String label;
	private Class<?> type;
    private Boolean isListItem = false;

	private final CheckType[] checkType;

	ParamDefinedInfo(String name, String label, Class<?> type, CheckType[] checkType) {
		super();
		this.name = name;
		this.label = label;
		this.type = type;
		this.checkType = checkType;
	}

    ParamDefinedInfo(String name, String label, Class<?> type, CheckType[] checkType, boolean isListItem) {
        super();
        this.name = name;
        this.label = label;
        this.type = type;
        this.checkType = checkType;
        this.isListItem = isListItem;
    }

	public String getName() {
		return name;
	}

	public String getLabel() {
		return label;
	}

	public Class<?> getType() {
		return type;
	}

	public CheckType[] getCheckType() {
		return checkType;
	}

    public Boolean getIsListItem() {
        return isListItem;
    }

    public static List<ParamDefinedInfo> getParamDefinedInfoList(Class<?> c, boolean just_defined) {
		List<ParamDefinedInfo> list = new LinkedList<ParamDefinedInfo>();
		do {
			Field[] fields = c.getDeclaredFields();
			for (Field f : fields) {

				ParamDefined pd = f.getAnnotation(ParamDefined.class);
				if (just_defined && null == pd) {
					continue;
				}
				ParamDefinedInfo paramDefinedInfo;
				if (null == pd) {
					paramDefinedInfo = new ParamDefinedInfo(f.getName(), "未知", f.getType(), new CheckType[] {});
				} else {
                    Type genericType = f.getGenericType();
                    //参数化类型，如 Collection<String>
                    if(genericType instanceof ParameterizedType){
                        paramDefinedInfo = new ParamDefinedInfo(f.getName(), pd.label(), (Class)((ParameterizedType)genericType).getActualTypeArguments()[0], pd.checkType(), true);
                    }else{
                        paramDefinedInfo = new ParamDefinedInfo(f.getName(), pd.label(), f.getType(), pd.checkType());
                    }
				}
				list.add(paramDefinedInfo);
			}
		} while (!Object.class.equals((c = c.getSuperclass())));
		return list;
	}
}