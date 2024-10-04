import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {InstructorsService} from "../../services/instructors.service";
import {catchError, Observable, throwError} from "rxjs";
import {PageResponse} from "../../model/page.response.model";
import {Instructor} from "../../model/instructor.model";
import {EmailExistsValidator} from "../../validators/emailexists.validator";
import {UsersService} from "../../services/users.service";
import {CoursesService} from "../../services/courses.service";
import {Course} from "../../model/course.model";

@Component({
  selector: 'app-teachers',
  templateUrl: './teachers.component.html',
  styleUrls: ['./teachers.component.css']
})


export class TeachersComponent implements OnInit {

  searchFormGroup!: FormGroup;
  instructorsFormGroup!: FormGroup;
  currentPage: number = 0;
  pageSize: number = 10;
  errorMessage!: string;
  pageInstructors!: Observable<PageResponse<Instructor>>;
  submitted: boolean = false;
  modalInstructor!: Instructor;
  pageCourses$!: Observable<PageResponse<Course>>;
  coursesCurrentPage: number = 0;
  coursesPageSize: number = 5;
  coursesErrorMessage!: string;

  constructor(private readonly modalService: NgbModal,
              private readonly fb: FormBuilder,
              private readonly instructorService: InstructorsService,
              private readonly userService: UsersService,
              private readonly courseService: CoursesService) {}

  ngOnInit(): void {
    this.searchFormGroup = this.fb.group({
      keyword: this.fb.control('')
    })
    this.instructorsFormGroup = this.fb.group({
      firstName: ["", Validators.required],
      lastName: ["", Validators.required],
      summary: ["", Validators.required],
      user: this.fb.group({
        email: ["", [Validators.required, Validators.pattern("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$")],
        [EmailExistsValidator.validate(this.userService)]],
        password: ["", Validators.required],
      })
    })
    this.handleSearchInstructors();
  }


  getModal(content: any){
    this.submitted = false;
    this.modalService.open(content, { size: 'xl' })
  }


  handleSearchInstructors() {
    let keyword = this.searchFormGroup.value.keyword;
    this.pageInstructors = this.instructorService.searchInstructors(keyword, this.currentPage, this.pageSize)
      .pipe(catchError(err => {
        this.errorMessage = err.message;
        return throwError(() => err);
      }));
  }

  goToPage(page: number) {
    this.currentPage = page;
    this.handleSearchInstructors();
  }

  handleDeleInstructor(instructor: Instructor) {
    let conf = confirm("Are you sure?");
    if(!conf) return;
    this.instructorService.deleteInstructor(instructor.instructorId).subscribe({
      next: () => {
        this.handleSearchInstructors();
      },
      error: err => {
        alert(err.message);
        console.log(err);
      }
    })
  }

  onCloseModal(modal: any) {
    modal.close();
    this.instructorsFormGroup.reset();
  }

  onSaveInstructor(modal: any) {
    console.log(this.instructorsFormGroup)
    this.submitted = true;
    if(this.instructorsFormGroup.invalid) return;
    this.instructorService.saveInstructor(this.instructorsFormGroup.value).subscribe({
      next:() => {
        alert("Sucess saving Instructor");
        this.handleSearchInstructors();
        this.instructorsFormGroup.reset();
        this.submitted = false;
        modal.close();
      }, error: err => {
        alert(err.message)
        console.log(err);
      }
    })
  }

  getCoursesModal(instructor: Instructor, coursesContent: any) {
    this.coursesCurrentPage = 0;
    this.modalInstructor = instructor;
    this.handleSearchCourses(instructor);
    this.modalService.open(coursesContent, {size: 'xl'});
  }

  private handleSearchCourses(instructor: Instructor) {
    this.pageCourses$ = this.courseService.getCoursesByInstructor(instructor.instructorId, this.coursesCurrentPage, this.coursesPageSize)
      .pipe(catchError(err => {
        this.coursesErrorMessage = err.message;
        return throwError(() => err);
      }))
  }

  goToCoursesPage(page: number) {
    this.coursesCurrentPage = page;
    this.handleSearchCourses(this.modalInstructor);
  }
}

