package com.blax.unit.testing.playground;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xbill.DNS.Address;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "v1")
public class DnsLookupService {

    @RequestMapping(value = "dns-lookup", method = RequestMethod.GET)
    ResponseEntity<List<AddressResult>> getIps(@RequestParam(name = "hostname", required = true) String hostname) {
        try {
            if (!validateHostName(hostname)) {
                throw new RuntimeException("Illegal Hostname");
            }
            // InetAddress[] allByName = InetAddress.getAllByName(hostname);

            InetAddress[] allByName = Address.getAllByName(hostname);
            List<AddressResult> results = Arrays.stream(allByName).map(
                    address -> new AddressResult(address.getHostAddress(), address.getHostName(), address.getCanonicalHostName())
            ).collect(Collectors.toList());

            return ResponseEntity.ok(results);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean validateHostName(String hostname) {
        return true;
    }

}

@Data
@RequiredArgsConstructor
class AddressResult {
    private final String ip;
    private final String host;
    private final String canonical;
}
