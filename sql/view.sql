-- View: tm.dv_teacher_active

-- DROP VIEW tm.dv_teacher_active;

CREATE OR REPLACE VIEW tm.dv_teacher_active AS
 WITH active_term AS (
         SELECT term.id
           FROM ea.term
          WHERE term.active IS TRUE
        ), course_teacher AS (
         SELECT DISTINCT courseclass.teacher_id,
            courseclass.term_id AS termid
           FROM ea.task_schedule schedule
             JOIN ea.task task ON schedule.task_id = task.id
             JOIN ea.course_class courseclass ON task.course_class_id = courseclass.id
             JOIN ea.course course_1 ON courseclass.course_id::text = course_1.id::text
        ), active_teacher AS (
         SELECT DISTINCT courseteacher.id AS teacher_id,
            courseteacher.name AS teacher_name,
            courseteacher.academic_title,
            department.name AS department_name,
            courseclass.term_id AS termid
           FROM ea.task_schedule schedule
             JOIN ea.task task ON schedule.task_id = task.id
             JOIN ea.course_class courseclass ON task.course_class_id = courseclass.id
             JOIN ea.course course_1 ON courseclass.course_id::text = course_1.id::text
             JOIN ea.teacher courseteacher ON courseclass.teacher_id::text = courseteacher.id::text
             JOIN ea.department department ON courseteacher.department_id::text = department.id::text
          WHERE courseclass.term_id = (( SELECT active_term.id
                   FROM active_term)) AND schedule.end_week::double precision > (( SELECT date_part('week'::text, now()) - date_part('week'::text, term.start_date) + 1::double precision AS d
                   FROM ea.term
                  WHERE term.active IS TRUE))
        ), new_teacher AS (
         SELECT course_teacher.teacher_id
           FROM course_teacher
          GROUP BY course_teacher.teacher_id
         HAVING min(course_teacher.termid) = (( SELECT active_term.id
                   FROM active_term))
        ), inspect4 AS (
         SELECT DISTINCT inspector.teachercode AS teacher_id
           FROM tm.supervisor_history inspector
          WHERE inspector.teachercode IS NOT NULL AND inspector.type::text = '督导'::text AND (inspector.term_id + 20) > (( SELECT active_term.id
                   FROM active_term))
        UNION
         SELECT DISTINCT supervisor.teacher_id
           FROM tm.supervisor_lecture_record supervisor
             JOIN tm.supervisor_role role ON supervisor.supervisor_role_id = role.id
             JOIN ea.task_schedule schedule ON supervisor.task_schedule_id = schedule.id
             JOIN ea.task ON schedule.task_id = task.id
             JOIN ea.course_class courseclass ON task.course_class_id = courseclass.id
          WHERE role.name::text = '校督导'::text AND (courseclass.term_id + 20) > (( SELECT active_term.id
                   FROM active_term))
        )
 SELECT DISTINCT active.teacher_id,
    active.teacher_name,
    active.department_name,
    active.academic_title,
    a.teacher_id AS isnew,
    inspect4.teacher_id AS has_supervisor
   FROM active_teacher active
     LEFT JOIN new_teacher a ON active.teacher_id::text = a.teacher_id::text
     LEFT JOIN inspect4 ON active.teacher_id::text = inspect4.teacher_id::text;

ALTER TABLE tm.dv_teacher_active
  OWNER TO tm;
COMMENT ON VIEW tm.dv_teacher_active
  IS '本学期当前周还有课的老师';


  -- View: tm.dv_teacher_role

-- DROP VIEW tm.dv_teacher_role;

CREATE OR REPLACE VIEW tm.dv_teacher_role AS
 WITH admin_class_at_school AS (
         SELECT ac.supervisor_id,
            ac.counsellor_id
           FROM ea.admin_class ac
             JOIN ea.major m ON ac.major_id = m.id
             JOIN ea.subject s ON m.subject_id::text = s.id::text
          WHERE 'now'::text::date < make_date(m.grade + s.length_of_schooling, 7, 1)
        )
 SELECT t.id AS user_id,
    'ROLE_IN_SCHOOL_TEACHER'::text AS role_id
   FROM ea.teacher t
  WHERE t.at_school = true
UNION ALL
 SELECT t.id AS user_id,
    'ROLE_SUBJECT_DIRECTOR'::text AS role_id
   FROM ea.teacher t
  WHERE (EXISTS ( SELECT subject_settings.subject_id,
            subject_settings.director_id,
            subject_settings.secretary_id
           FROM tm.subject_settings
          WHERE subject_settings.director_id::text = t.id::text))
UNION ALL
 SELECT t.id AS user_id,
    'ROLE_SUBJECT_SECRETARY'::text AS role_id
   FROM ea.teacher t
  WHERE (EXISTS ( SELECT subject_settings.subject_id,
            subject_settings.director_id,
            subject_settings.secretary_id
           FROM tm.subject_settings
          WHERE subject_settings.secretary_id::text = t.id::text))
UNION ALL
 SELECT t.id AS user_id,
    'ROLE_COURSE_TEACHER'::text AS role_id
   FROM ea.teacher t
  WHERE (EXISTS ( SELECT course_class.id,
            course_class.assess_type,
            course_class.code,
            course_class.course_id,
            course_class.department_id,
            course_class.enabled,
            course_class.end_week,
            course_class.property_id,
            course_class.start_week,
            course_class.teacher_id,
            course_class.term_id,
            course_class.test_type,
            course_class.period_experiment,
            course_class.period_theory,
            course_class.period_weeks,
            task.id,
            task.code,
            task.course_class_id,
            task.course_item_id,
            task.end_week,
            task.is_primary,
            task.start_week,
            task_schedule.id,
            task_schedule.day_of_week,
            task_schedule.end_week,
            task_schedule.odd_even,
            task_schedule.place_id,
            task_schedule.start_section,
            task_schedule.start_week,
            task_schedule.task_id,
            task_schedule.teacher_id,
            task_schedule.total_section
           FROM ea.course_class
             JOIN ea.task ON task.course_class_id = course_class.id
             JOIN ea.task_schedule ON task_schedule.task_id = task.id
          WHERE course_class.term_id = (( SELECT term.id
                   FROM ea.term
                  WHERE term.active = true)) AND task_schedule.teacher_id::text = t.id::text))
UNION ALL
 SELECT t.id AS user_id,
    'ROLE_PLACE_BOOKING_CHECKER'::text AS role_id
   FROM ea.teacher t
     JOIN tm.booking_auth ba ON ba.checker_id::text = t.id::text
UNION ALL
 SELECT t.id AS user_id,
    'ROLE_CLASS_SUPERVISOR'::text AS role_id
   FROM ea.teacher t
  WHERE (EXISTS ( SELECT admin_class_at_school.supervisor_id,
            admin_class_at_school.counsellor_id
           FROM admin_class_at_school
          WHERE admin_class_at_school.supervisor_id::text = t.id::text))
UNION ALL
 SELECT t.id AS user_id,
    'ROLE_STUDENT_COUNSELLOR'::text AS role_id
   FROM ea.teacher t
  WHERE (EXISTS ( SELECT admin_class_at_school.supervisor_id,
            admin_class_at_school.counsellor_id
           FROM admin_class_at_school
          WHERE admin_class_at_school.counsellor_id::text = t.id::text))
UNION ALL
 SELECT s.teacher_id AS user_id,
    'ROLE_SUPERVISOR_ADMIN'::text AS role_id
   FROM tm.supervisor s
  WHERE s.role_type = 0
UNION ALL
 SELECT DISTINCT s.teacher_id AS user_id,
    'ROLE_SUPERVISOR'::text AS role_id
   FROM tm.supervisor s
     JOIN tm.supervisor_role r ON s.role_type = r.id
     JOIN ea.term t ON s.term_id = t.id
  WHERE (r.name::text = '校督导'::text OR r.name::text = '院督导'::text) AND t.active IS TRUE
UNION ALL
 SELECT DISTINCT s.teacher_id AS user_id,
    'ROLE_SUPERVISOR_VIEWER'::text AS role_id
   FROM tm.supervisor s
     JOIN tm.supervisor_role r ON s.role_type = r.id
  WHERE r.name::text = '校督导'::text OR r.name::text = '院督导'::text;

ALTER TABLE tm.dv_teacher_role
  OWNER TO tm;

/*历史遗留数据视图*/
CREATE OR REPLACE VIEW tm.dv_supervisor_history AS
 SELECT *
   FROM tm.supervisor_history;

ALTER TABLE tm.dv_supervisor_history
  OWNER TO tm;

  /*将新旧数据联合在一起查询*/
  CREATE OR REPLACE VIEW tm.dv_supervise_public AS
 SELECT dv_supervise_view.id,
    false AS is_legacy,
    dv_supervise_view.supervisor_date,
    dv_supervise_view.evaluate_level,
    dv_supervise_view.type_name,
    dv_supervise_view.termid AS term_id,
    dv_supervise_view.department_name,
    dv_supervise_view.teacher_id,
    dv_supervise_view.teacher_name,
    dv_supervise_view.course_name,
    concat('星期', "substring"('一二三四五六日'::text, dv_supervise_view.day_of_week, 1), ' ', dv_supervise_view.start_section::text, '-', (dv_supervise_view.start_section + dv_supervise_view.total_section - 1)::text, '节 ', dv_supervise_view.place_name) AS course_other_info
   FROM tm.dv_supervise_view
  WHERE dv_supervise_view.status = 2
UNION ALL
 SELECT dv_supervisor_history.id,
    true AS is_legacy,
    dv_supervisor_history.listentime AS supervisor_date,
    dv_supervisor_history.evaluategrade AS evaluate_level,
    dv_supervisor_history.type AS type_name,
    dv_supervisor_history.term_id,
    dv_supervisor_history.collegename AS department_name,
    dv_supervisor_history.teachercode AS teacher_id,
    dv_supervisor_history.teachername AS teacher_name,
    dv_supervisor_history.coursename AS course_name,
    dv_supervisor_history.classpostion AS course_other_info
   FROM tm.dv_supervisor_history
  WHERE dv_supervisor_history.state::text = 'yes'::text;

ALTER TABLE tm.dv_supervise_public
  OWNER TO tm;

