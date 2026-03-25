package com.chendev.dishdash.domain.address;

import com.chendev.dishdash.common.exception.ErrorCode;
import com.chendev.dishdash.common.exception.ResourceNotFoundException;
import com.chendev.dishdash.domain.address.dto.AddressRequest;
import com.chendev.dishdash.domain.address.dto.AddressResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryAddressService {

    private final DeliveryAddressRepository addressRepository;

    @Transactional(readOnly = true)
    public List<AddressResponse> listByCustomer(Long customerId) {
        return addressRepository.findAllByCustomerId(customerId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AddressResponse getById(Long id, Long customerId) {
        return toResponse(findOwnedOrThrow(id, customerId));
    }

    @Transactional
    public AddressResponse create(AddressRequest request, Long customerId) {
        // If this is the customer's first address, make it default automatically.
        boolean hasExisting = !addressRepository.findAllByCustomerId(customerId).isEmpty();
        boolean shouldBeDefault = !hasExisting || Boolean.TRUE.equals(request.getIsDefault());

        if (shouldBeDefault) {
            addressRepository.clearDefaultByCustomerId(customerId);
        }

        DeliveryAddress address = DeliveryAddress.builder()
                .customerId(customerId)
                .label(request.getLabel())
                .recipient(request.getRecipient())
                .phone(request.getPhone())
                .streetLine1(request.getStreetLine1())
                .streetLine2(request.getStreetLine2())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .isDefault(shouldBeDefault)
                .build();

        return toResponse(addressRepository.save(address));
    }

    @Transactional
    public AddressResponse update(Long id, AddressRequest request, Long customerId) {
        DeliveryAddress address = findOwnedOrThrow(id, customerId);

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressRepository.clearDefaultByCustomerId(customerId);
            address.setIsDefault(true);
        }

        address.setLabel(request.getLabel());
        address.setRecipient(request.getRecipient());
        address.setPhone(request.getPhone());
        address.setStreetLine1(request.getStreetLine1());
        address.setStreetLine2(request.getStreetLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setZipCode(request.getZipCode());

        return toResponse(addressRepository.save(address));
    }

    @Transactional
    public void delete(Long id, Long customerId) {
        DeliveryAddress address = findOwnedOrThrow(id, customerId);
        addressRepository.delete(address);
        log.info("Address deleted: id={}, customerId={}", id, customerId);
    }

    @Transactional
    public AddressResponse setDefault(Long id, Long customerId) {
        findOwnedOrThrow(id, customerId);
        addressRepository.clearDefaultByCustomerId(customerId);

        DeliveryAddress address = findOwnedOrThrow(id, customerId);
        address.setIsDefault(true);
        return toResponse(addressRepository.save(address));
    }

    private DeliveryAddress findOwnedOrThrow(Long id, Long customerId) {
        return addressRepository.findByIdAndCustomerId(id, customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.ADDRESS_NOT_FOUND, "id=" + id));
    }

    private AddressResponse toResponse(DeliveryAddress address) {
        return AddressResponse.builder()
                .id(address.getId())
                .label(address.getLabel())
                .recipient(address.getRecipient())
                .phone(address.getPhone())
                .streetLine1(address.getStreetLine1())
                .streetLine2(address.getStreetLine2())
                .city(address.getCity())
                .state(address.getState())
                .zipCode(address.getZipCode())
                .isDefault(address.getIsDefault())
                .build();
    }
}