
    create sequence achievements_SEQ start with 1 increment by 50;

    create sequence challenge_progress_SEQ start with 1 increment by 50;

    create sequence challenges_SEQ start with 1 increment by 50;

    create sequence food_entries_SEQ start with 1 increment by 50;

    create sequence nutrition_plans_SEQ start with 1 increment by 50;

    create sequence psychological_tips_SEQ start with 1 increment by 50;

    create sequence user_achievements_SEQ start with 1 increment by 50;

    create sequence user_profiles_SEQ start with 1 increment by 50;

    create sequence users_SEQ start with 1 increment by 50;

    create table achievements (
        id bigint not null,
        description varchar(255),
        icon varchar(255),
        name varchar(255) not null,
        points_value integer,
        type varchar(255) check (type in ('STREAK','CHALLENGE_COMPLETION','NUTRITION_GOAL','COMMUNITY')),
        primary key (id)
    );

    create table challenge_progress (
        id bigint not null,
        current_progress integer,
        end_date date,
        start_date date,
        status varchar(255) check (status in ('NOT_STARTED','IN_PROGRESS','COMPLETED','FAILED')),
        target_progress integer,
        challenge_id bigint,
        user_id bigint,
        primary key (id)
    );

    create table challenge_tips (
        challenge_id bigint not null,
        tip varchar(255)
    );

    create table challenges (
        id bigint not null,
        completion_criteria varchar(255),
        created_at timestamp(6),
        description varchar(255),
        difficulty varchar(255) check (difficulty in ('EASY','MEDIUM','HARD')),
        duration_days integer,
        points_reward integer,
        title varchar(255) not null,
        type varchar(255) check (type in ('NUTRITION','EXERCISE','MINDFULNESS','HABIT')),
        primary key (id)
    );

    create table food_entries (
        id bigint not null,
        calories float(53),
        carbs float(53),
        description varchar(255),
        eating_context varchar(255),
        fat float(53),
        meal_type varchar(255) check (meal_type in ('BREAKFAST','LUNCH','DINNER','SNACK')),
        mood_after varchar(255),
        mood_before varchar(255),
        photo_id varchar(255),
        protein float(53),
        timestamp timestamp(6),
        was_hungry boolean,
        user_id bigint,
        primary key (id)
    );

    create table nutrition_plan_meals (
        plan_id bigint not null,
        meal_plan varchar(255),
        meal_type varchar(255) not null check (meal_type in ('BREAKFAST','LUNCH','DINNER','SNACK')),
        primary key (plan_id, meal_type)
    );

    create table nutrition_plan_recommendations (
        plan_id bigint not null,
        recommendation varchar(255)
    );

    create table nutrition_plan_seasonal_foods (
        plan_id bigint not null,
        seasonal_food varchar(255)
    );

    create table nutrition_plans (
        id bigint not null,
        carbs_target float(53),
        daily_calories float(53),
        fat_target float(53),
        protein_target float(53),
        user_id bigint not null unique,
        primary key (id)
    );

    create table psychological_tips (
        id bigint not null,
        content varchar(255),
        created_at timestamp(6),
        min_level integer,
        title varchar(255) not null,
        trigger_condition varchar(255),
        type varchar(255) check (type in ('MOTIVATIONAL','EDUCATIONAL','MINDFULNESS','BEHAVIORAL')),
        primary key (id)
    );

    create table user_achievements (
        id bigint not null,
        earned_at timestamp(6),
        achievement_id bigint,
        user_id bigint,
        primary key (id)
    );

    create table user_allergies (
        profile_id bigint not null,
        allergy varchar(255)
    );

    create table user_dietary_restrictions (
        profile_id bigint not null,
        restriction varchar(255)
    );

    create table user_disliked_foods (
        profile_id bigint not null,
        disliked_food varchar(255)
    );

    create table user_food_preferences (
        profile_id bigint not null,
        preference varchar(255)
    );

    create table user_profiles (
        id bigint not null,
        activity_level varchar(255) check (activity_level in ('SEDENTARY','LIGHTLY_ACTIVE','MODERATELY_ACTIVE','VERY_ACTIVE','EXTREMELY_ACTIVE')),
        age integer,
        eats_out_frequency boolean,
        gender varchar(255),
        height float(53),
        meals_per_day integer,
        primary_goal varchar(255) check (primary_goal in ('WEIGHT_LOSS','MUSCLE_GAIN','MAINTENANCE','HEALTH_IMPROVEMENT')),
        weight float(53),
        user_id bigint not null unique,
        primary key (id)
    );

    create table user_typical_meal_times (
        profile_id bigint not null,
        meal_time time(0),
        day_of_week varchar(255) not null check (day_of_week in ('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY')),
        primary key (profile_id, day_of_week)
    );

    create table users (
        id bigint not null,
        communication_style varchar(255) check (communication_style in ('MENTOR','BUDDY')),
        created_at timestamp(6),
        first_name varchar(255),
        last_active_at timestamp(6),
        last_name varchar(255),
        state varchar(255) check (state in ('ONBOARDING','ACTIVE','PAUSED','CHURNED')),
        telegram_id bigint not null unique,
        username varchar(255),
        primary key (id)
    );

    alter table if exists challenge_progress 
       add constraint FKrthk9ta60s41rtht68svmcyvg 
       foreign key (challenge_id) 
       references challenges;

    alter table if exists challenge_progress 
       add constraint FKe8fh3gr3mmgifgjbslaujmv6a 
       foreign key (user_id) 
       references users;

    alter table if exists challenge_tips 
       add constraint FKq4uhbceuis12lmjglpaseisju 
       foreign key (challenge_id) 
       references challenges;

    alter table if exists food_entries 
       add constraint FKdq1g031i660pshi5p37ts7cb7 
       foreign key (user_id) 
       references users;

    alter table if exists nutrition_plan_meals 
       add constraint FKaykot5pevoi806cejfsxpqkim 
       foreign key (plan_id) 
       references nutrition_plans;

    alter table if exists nutrition_plan_recommendations 
       add constraint FKmtiglpkrtwfwhgloj0nb0l654 
       foreign key (plan_id) 
       references nutrition_plans;

    alter table if exists nutrition_plan_seasonal_foods 
       add constraint FKfqfltgiwwa0qmn3ruqa2it8os 
       foreign key (plan_id) 
       references nutrition_plans;

    alter table if exists nutrition_plans 
       add constraint FKp24rwm221u5myvof88q7ybmtm 
       foreign key (user_id) 
       references users;

    alter table if exists user_achievements 
       add constraint FK8ipvec6cs8t3g8515thtlsxuf 
       foreign key (achievement_id) 
       references achievements;

    alter table if exists user_achievements 
       add constraint FK6vt5fpu0uta41vny1x6vpk45k 
       foreign key (user_id) 
       references users;

    alter table if exists user_allergies 
       add constraint FKdx3tk0soa9gj8p686y85w7w0 
       foreign key (profile_id) 
       references user_profiles;

    alter table if exists user_dietary_restrictions 
       add constraint FK8l7s0mrxmnd68v2h7c8kw3iwv 
       foreign key (profile_id) 
       references user_profiles;

    alter table if exists user_disliked_foods 
       add constraint FKgiy0o5jmkj74iyil6ahtm9af7 
       foreign key (profile_id) 
       references user_profiles;

    alter table if exists user_food_preferences 
       add constraint FKpaip9fsm8nnfl2aik29ga8uik 
       foreign key (profile_id) 
       references user_profiles;

    alter table if exists user_profiles 
       add constraint FKjcad5nfve11khsnpwj1mv8frj 
       foreign key (user_id) 
       references users;

    alter table if exists user_typical_meal_times 
       add constraint FKrf9gvvt74tjcsuyuedcdtbsnt 
       foreign key (profile_id) 
       references user_profiles;
