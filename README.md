# GalleryCycleImageView
GalleryCycleImageView 是一个轻量级的画廊效果的轮播图，自带指示器，支持无限循环，使用简单。源码简单、注释丰富，可根据需求修改源码实现其他需求
# 更新日志
2017-6-7 创建项目
# 如何使用
* Gradle
```
compile 'com.wheat7:gallerycycleimageview:0.0.1'
```
* 可下载项目，复制GalleryCycleView类以及attr_gallery_cycle_image就可以使用，源码注释丰富，可修改源码实现其他需求

# 使用方法
* 属性
```xml
        <attr name="indicatorFocusColor" format="color"/>
        <attr name="indicatorNormalColor" format="color"/>
        <attr name="indicatorSize" format="dimension"/>
        <attr name="duration" format="integer" />
        <attr name="viewPagerHeight" format="dimension"/>
        <attr name="pageMargin" format="dimension" />
```
直接在Layout中引用,目前属性不能为空
```xml
    <com.wheat7.library.GalleryCycleImageView
        android:id="@+id/gallery_cycle_image"
        android:layout_width="match_parent"
        android:layout_height="180dp"
        android:layout_marginTop="10dp"
        android:clipChildren="false"
        android:gravity="center"
        app:duration="5000"
        app:indicatorFocusColor="#D43D3D"
        app:indicatorNormalColor="#D3D3D3"
        app:indicatorSize="8dp"
        app:pageMargin="10dp"
        app:viewPagerHeight="160dp"/>
```

* 在代码中设置
```Java
...
private GalleryCycleImageView mGalleryCycleImageView;
...
        mGalleryCycleImageView = (GalleryCycleImageView) findViewById(R.id.gallery_cycle_image);
        //必须setCount以添加指示器,这里写死了
        mGalleryCycleImageView.setCount(6);
        //实现接口，为ImageView传入数据，可结合图片加载框架加载图片，也可以返回一个自定义的View，自由实现
        mGalleryCycleImageView.setGalleyCycleImageListener(new GalleryCycleImageView.GalleyCycleImageListener() {
            @Override
            public View setItem(View imageView, final int position) {
                imageView.setBackgroundResource(R.drawable.dali);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext,"Position=" + position,Toast.LENGTH_SHORT).show();
                    }
                });
                return imageView;
            }
        });
        mGalleryCycleImageView.startAutoPlay();
...
```

# Licence
Apache-2.0

