# MateraView  (v0.1)
![MateraView is a flexible, easy to use, all in one design library for your android project.](https://github.com/arathus/materaview/blob/master/gfx/banner.png)
#### Do you want your **app look stylish**, but you are not a designer ? Or just don't want to spend hours of creating **Material designed backgrounds**? Do you need **flexibility**, and also want a **simple and easy** to understand design library? MateraView was designed to solve these problems!

# Screenshots

![demo1](https://github.com/arathus/materaview/blob/master/gfx/screenshots.png)

### Why is it useful ? 
- takes about **2 minutes** to integrate
- fits perfectly to the **Material Design** guideline
- you can customize your own view with **various styles** 
- **suits** to all kind of Layouts

##### Never spend time with creating single-use design pictures again!

# Setup

### 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

### 2. Provide the gradle dependency
Add the dependency:
```gradle
	dependencies {
	        implementation 'com.github.arathus:materaview:0.1'
	}
```

### 3. Add the MateraView to XML...
```xml
<com.arathus.matera.MateraView
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```

### 4. ...or to your code
```java
LinearLayout parentLayout = findViewById(R.id.parent);

new MateraView.Builder(this).setParentView(parent).build();
```
Great. MateraView is now ready to use, and customize.
# Samples

```xml
    <com.arathus.matera.MateraView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:dropShadowSize="5"
        app:elementColor="@color/colorSecond"
        app:elementSizes="normal"
        app:firstCornerToDraw="TOP_RIGHT"
        app:materaStyle="integral_equal"
        app:numberOfLayers="5"
        app:secondCornerToDraw="BOTTOM_LEFT"
        app:shadeOfColor="5" />
```
![demo1](https://github.com/arathus/materaview/blob/master/gfx/1st_sample.png)


```java
ConstraintLayout constraintLayout = findViewById(R.id.constraint);

new MateraView.Builder(this)
        .setNumberOfLayers(4)
        .setParentView(constraintLayout)
        .setStyle(MateraView.FRAME_STRICT)
        .setFirstCornerToDraw(CornerElement.TOP_LEFT)
        .setSecondCornerToDraw(CornerElement.BOTTOM_RIGHT)
        .setColor("#0277BD")
        .build();
```
![demo1](https://github.com/arathus/materaview/blob/master/gfx/2nd_sample.png)



## Properties:

* `app:materaStyle`
* `app:elementSizes` 
* `app:numberOfLayers`
* `app:dropShadowSize`
* `app:elementColor`
* `app:backgroundColor`
* `app:inverseColors`
* `app:shadeOfColor`
* `app:blackFilter`
* `app:filterValue`
* `app:firstCornerToDraw`
* `app:secondCornerToDraw`
* `app:thirdCornerToDraw`
* `app:fourthCornerToDraw`

# FAQ

#### Does I get the same view every time I use it ?
There are styles in the view which are static, and some when the view is created you get a new MateraView with random generated layer elements according to your predefined settings and to the material design guideline.

#### Does it make the app slow ?
The cordinates of the elements, and the colors are created and saved in the constructor of the View; after it is done the library just draw the preset values. The MateraView was built from basic elements, therefore the library only use 1-3% of the processor

#### Can I save the generated designs ?
At this point of the development you are unable to do it, but it's possible that in the further versions this will be an option

# Credits

- ##### Danny Hvam - [GitHub](https://github.com/oizo)
	- For his awesome material color palette generator library [Material Color Creator](https://github.com/shopgun/material-color-creator-android). Thanks for the help!
	
# Developed By

 * Ákos Jakub (Arathus)
 * <jakubakos@gmail.com>
 * [paypal.me/arathus](http://paypal.me/arathus)
 
# License

    Copyright 2018 Ákos Jakub

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
