package bc_server;

/**
 * 学生信息
 * @author yank
 *
 */
public class Student {
    private Integer id;
    private String name;
    private Integer age;
    
    /**
     * 无参构造函数
     */
    public Student(){
        
    }
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
}
