cmake_minimum_required(VERSION 3.22.1)

project("logcatcher")

add_library(${CMAKE_PROJECT_NAME} SHARED
        logcat.cpp)
target_link_libraries(${CMAKE_PROJECT_NAME}
    android
    log)
