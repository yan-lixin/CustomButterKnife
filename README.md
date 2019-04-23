## API


|方法|描述|
|:---|:---|
| getEnclosedElements()  |返回该元素直接包含的子元素|
| getEnclosingElement()  |返回包含该element的父element，与上一个方法相反|
| getKind()  |返回element的类型，判断是哪种element|
| getModifiers()  |获取修饰关键字，如public static final等关键字|
| getSimpleName()  |获取名字，不含包名|
| getQualifiedName()  |获取全名，如果是类的话，包含完整的包名路径|
| getParameters()  |获取方法的参数元素，每个元素是一个VariableElement|
| getReturnType()  |获取方法元素的返回值|
| getConstantValue()  |如果属性变量被final修饰，则可以使用该方法获取它的值|
