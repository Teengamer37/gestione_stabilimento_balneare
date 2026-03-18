module com.example.s_balneare {
    requires java.sql;
    requires java.desktop;
    requires java.xml.crypto;
    requires bcrypt;

    exports com.example.s_balneare;
    exports com.example.s_balneare.domain.user;
    exports com.example.s_balneare.domain.beach;
    exports com.example.s_balneare.domain.layout;
    exports com.example.s_balneare.domain.booking;
    exports com.example.s_balneare.domain.review;
    exports com.example.s_balneare.domain.moderation;
    exports com.example.s_balneare.domain.common;

    exports com.example.s_balneare.application.service.user;
    exports com.example.s_balneare.application.port.out;
    exports com.example.s_balneare.application.port.in.user;
    exports com.example.s_balneare.application.port.out.user;
    exports com.example.s_balneare.application.port.in.beach;
    exports com.example.s_balneare.application.port.out.booking;
    exports com.example.s_balneare.application.port.in.booking;
    exports com.example.s_balneare.application.port.out.beach;
    exports com.example.s_balneare.application.port.in.moderation;
    exports com.example.s_balneare.application.port.out.common;
    exports com.example.s_balneare.application.port.in.common;
    exports com.example.s_balneare.application.port.out.moderation;
    exports com.example.s_balneare.application.port.out.review;

    opens com.example.s_balneare.domain.beach;
    opens com.example.s_balneare.domain.booking;
    opens com.example.s_balneare.domain.common;
    opens com.example.s_balneare.domain.layout;
    opens com.example.s_balneare.domain.moderation;
    opens com.example.s_balneare.domain.review;
    opens com.example.s_balneare.domain.user;

    opens com.example.s_balneare.application.service.beach;
    opens com.example.s_balneare.application.service.booking;
    opens com.example.s_balneare.application.service.common;
    opens com.example.s_balneare.application.service.moderation;
    opens com.example.s_balneare.application.service.review;
    opens com.example.s_balneare.application.service.user;
}