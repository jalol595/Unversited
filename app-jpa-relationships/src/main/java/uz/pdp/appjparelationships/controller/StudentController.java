package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.entity.Subject;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    SubjectRepository subjectRepository;

    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage;
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
        return studentPage;
    }

    //3. FACULTY DEKANAT
    @GetMapping("/forDekanat/{facultyId}")
    public Page<Student> getStudentFroFaculty(@PathVariable Integer facultyId, @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> allByGroup_faculty_id = studentRepository.findAllByGroup_Faculty_Id(facultyId, pageable);
        return allByGroup_faculty_id;
    }


    //4. GROUP OWNER

    @GetMapping("/forGroup/{groupId}")
    public Page<Student> getStudentForGroup(@PathVariable Integer groupId, @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> allByGroup_id = studentRepository.findAllByGroup_Id(groupId, pageable);
        return allByGroup_id;
    }


    @PostMapping
    public String save(@RequestBody StudentDto studentDto) {
        boolean name = studentRepository.existsByFirstNameAndLastName(studentDto.getFirstName(), studentDto.getLastName());
        boolean street = addressRepository.existsByCityAndDistrictAndStreet(studentDto.getCity(), studentDto.getDistrict(), studentDto.getStreet());
        boolean byId = groupRepository.existsById(studentDto.getGroupId());

        if (name && street && byId) return "already exist";

        Student student = new Student();
        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());

        Address address=new Address(null, studentDto.getCity(), studentDto.getDistrict(), studentDto.getStreet());

        student.setAddress(address);

        Optional<Group> groupId = groupRepository.findById(studentDto.getGroupId());
        if (!groupId.isPresent()){
            return "not found group id";
        }

        Group group = groupId.get();

        student.setGroup(group);

        List<Subject> allById = subjectRepository.findAllById(studentDto.getSubjectsId());
        student.setSubjects(allById);

        studentRepository.save(student);
        return "saved";
    }

    @DeleteMapping("/{groupId}")
    public String delete(@PathVariable Integer groupId){
        try {
            studentRepository.deleteById(groupId);
            return "deleted";
        }catch (Exception e){
            return "error deleting";
        }
    }

    @PutMapping("/{groupId}")
    public String edit(@PathVariable Integer groupId, @RequestBody StudentDto studentDto){
        if(!studentRepository.existsById(groupId)){
            return "not found groupId";
        }
        Student student = new Student();
        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());

        Address address=new Address(null, studentDto.getCity(), studentDto.getDistrict(), studentDto.getStreet());

        student.setAddress(address);

        Optional<Group> groupId1 = groupRepository.findById(studentDto.getGroupId());
        if (!groupId1.isPresent()){
            return "not found group id";
        }

        Group group = groupId1.get();

        student.setGroup(group);

        List<Subject> allById = subjectRepository.findAllById(studentDto.getSubjectsId());
        student.setSubjects(allById);

        studentRepository.save(student);
        return "edit";


    }
}
