package com.helkor.project.connection.websocket.util;

public enum Command {

    c_requestRoute,
    c_cancelRouteRequest,
    c_sendRoute,

    s_sendRandomToken,

    s_notifyRouteRequested,
    s_notifyRouteRequestCanceled,

    s_sendRoute,
}
