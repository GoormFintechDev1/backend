package com.example.backend.converter;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

// Entity를 DTO로 변환하는 클래스
public class DTOConverter {

    // 단일 객체 변환 함수
    public static <D, E> D toDto(E entity, Function<E, D> converter) {
        return converter.apply(entity);
    }

    // 여러 개의 객체 변환 함수 (리스트 변환)
    public static <D, E> List<D> toDtoList(List<E> entities, Function<E, D> converter) {
        return entities.stream()  // 스트림 생성
                .map(converter)  // 각각의 객체를 변환
                .collect(Collectors.toList());  // 리스트로 수집
    }
}
