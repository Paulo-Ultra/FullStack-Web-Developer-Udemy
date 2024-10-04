import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {Instructor} from "../../model/instructor.model";
import {catchError, Observable, throwError} from "rxjs";
import {PageResponse} from "../../model/page.response.model";
import {Course} from "../../model/course.model";
import {CoursesService} from "../../services/courses.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-courses-instructor',
  templateUrl: './courses-instructor.component.html',
  styleUrls: ['./courses-instructor.component.css']
})
export class CoursesInstructorComponent implements OnInit {

  instructorId!: number;
  currentInstructor!: Instructor;
  pageCourses!: Observable<PageResponse<Course>>;
  currentPage: number = 0;
  pageSize: number = 5;
  errorMessage!: string;
  courseFormGroup!: FormGroup;
  submitted: boolean = false;
  updateCourseFormGroup!: FormGroup;

  constructor(private route: ActivatedRoute,
              private courseService: CoursesService,
              private fb: FormBuilder,
              private modalService: NgbModal) { }

  ngOnInit(): void {
    this.instructorId = this.route.snapshot.params['id'];
    this.fillCurrentInstructor();
    this.handleSearchInstructorCourses();
  }

  private fillCurrentInstructor(){
    this.currentInstructor = {
      instructorId: this.instructorId,
      firstName: "",
      lastName: "",
      summary: "",
      user: {
        email: "",
        password: ""
      }
    }
  }

  private handleSearchInstructorCourses(){
    this.pageCourses = this.courseService.getCoursesByInstructor(this.instructorId, this.currentPage, this.pageSize)
      .pipe(catchError(err => {
        this.errorMessage = err.message;
        return throwError(err);
      }));
  }

  goToPage(page: number) {
    this.currentPage = page;
    this.handleSearchInstructorCourses();
  }

  getModal(content: any) {
    this.submitted = false;
    this.courseFormGroup = this.fb.group({
      courseName: ['', Validators.required],
      courseDuration: ['', Validators.required],
      courseDescription: ['', Validators.required],
      instructor: [this.currentInstructor, Validators.required]
    })
    this.modalService.open(content, {size: 'xl'});
  }

  onCloseModal(modal: any) {
    modal.close();
    this.courseFormGroup.reset();
  }

  onSaveCourse(modal: any) {
    console.log(this.courseFormGroup);
    this.submitted = true;
    if(this.courseFormGroup.invalid) return;
    this.courseService.saveCourse(this.courseFormGroup.value).subscribe({
      next: () => {
        alert("Sucess Saving Course");
        this.handleSearchInstructorCourses();
        this.courseFormGroup.reset();
        this.submitted = false;
        modal.close();
      }, error: err => {
        alert(err.message)
        console.log(err);
      }
    })
  }

  getUpdateModal(course: Course, updateContent: any) {
    this.updateCourseFormGroup = this.fb.group({
      courseId: [course.courseId, Validators.required],
      courseName: [course.courseName, Validators.required],
      courseDuration: [course.courseDuration, Validators.required],
      courseDescription: [course.courseDescription, Validators.required],
      instructor: [course.instructor, Validators.required]
    });
    this.modalService.open(updateContent,{size: 'xl'});
  }

  onCloseUpdateModal(updateModal: any) {
    updateModal.close();
    this.updateCourseFormGroup.reset();
  }

  onUpdateCourse(updateModal: any) {
    this.submitted = true;
    if (this.updateCourseFormGroup.invalid) return;
    this.courseService.updateCourse(this.updateCourseFormGroup.value, this.updateCourseFormGroup.value.courseId)
      .subscribe({
        next: () => {
          alert("Sucess Saving Update Course");
          this.handleSearchInstructorCourses();
          this.submitted = false;
          updateModal.close();
        }, error: err => {
          console.log(err);
          alert(err.message);
        }
      });
  }
}
