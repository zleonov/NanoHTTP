package software.leonov.client.http;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MediaTypeTest {

    private static Set<String> ianaTypes = new HashSet<>();

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        ianaTypes.add("application/1d-interleaved-parityfec");
        ianaTypes.add("application/3gpdash-qoe-report+xml");
        ianaTypes.add("application/3gpp-ims+xml");
        ianaTypes.add("application/A2L");
        ianaTypes.add("application/AML");
        ianaTypes.add("application/ATF");
        ianaTypes.add("application/ATFX");
        ianaTypes.add("application/ATXML");
        ianaTypes.add("application/CALS-1840");
        ianaTypes.add("application/CDFX+XML");
        ianaTypes.add("application/CEA");
        ianaTypes.add("application/CSTAdata+xml");
        ianaTypes.add("application/DCD");
        ianaTypes.add("application/DII");
        ianaTypes.add("application/DIT");
        ianaTypes.add("application/EDI-X12");
        ianaTypes.add("application/EDI-consent");
        ianaTypes.add("application/EDIFACT");
        ianaTypes.add("application/EmergencyCallData.Comment+xml");
        ianaTypes.add("application/EmergencyCallData.Control+xml");
        ianaTypes.add("application/EmergencyCallData.DeviceInfo+xml");
        ianaTypes.add("application/EmergencyCallData.ProviderInfo+xml");
        ianaTypes.add("application/EmergencyCallData.ServiceInfo+xml");
        ianaTypes.add("application/EmergencyCallData.SubscriberInfo+xml");
        ianaTypes.add("application/EmergencyCallData.VEDS+xml");
        ianaTypes.add("application/EmergencyCallData.eCall.MSD");
        ianaTypes.add("application/H224");
        ianaTypes.add("application/IOTP");
        ianaTypes.add("application/ISUP");
        ianaTypes.add("application/LXF");
        ianaTypes.add("application/MF4");
        ianaTypes.add("application/ODA");
        ianaTypes.add("application/ODX");
        ianaTypes.add("application/PDX");
        ianaTypes.add("application/QSIG");
        ianaTypes.add("application/SGML");
        ianaTypes.add("application/TETRA_ISI");
        ianaTypes.add("application/activemessage");
        ianaTypes.add("application/activity+json");
        ianaTypes.add("application/alto-costmap+json");
        ianaTypes.add("application/alto-costmapfilter+json");
        ianaTypes.add("application/alto-directory+json");
        ianaTypes.add("application/alto-endpointcost+json");
        ianaTypes.add("application/alto-endpointcostparams+json");
        ianaTypes.add("application/alto-endpointprop+json");
        ianaTypes.add("application/alto-endpointpropparams+json");
        ianaTypes.add("application/alto-error+json");
        ianaTypes.add("application/alto-networkmap+json");
        ianaTypes.add("application/alto-networkmapfilter+json");
        ianaTypes.add("application/andrew-inset");
        ianaTypes.add("application/applefile");
        ianaTypes.add("application/atom+xml");
        ianaTypes.add("application/atomcat+xml");
        ianaTypes.add("application/atomdeleted+xml");
        ianaTypes.add("application/atomicmail");
        ianaTypes.add("application/atomsvc+xml");
        ianaTypes.add("application/atsc-dwd+xml");
        ianaTypes.add("application/atsc-held+xml");
        ianaTypes.add("application/atsc-rdt+json");
        ianaTypes.add("application/atsc-rsat+xml");
        ianaTypes.add("application/auth-policy+xml");
        ianaTypes.add("application/bacnet-xdd+zip");
        ianaTypes.add("application/batch-SMTP");
        ianaTypes.add("application/beep+xml");
        ianaTypes.add("application/calendar+json");
        ianaTypes.add("application/calendar+xml");
        ianaTypes.add("application/call-completion");
        ianaTypes.add("application/cbor");
        ianaTypes.add("application/cbor-seq");
        ianaTypes.add("application/cccex");
        ianaTypes.add("application/ccmp+xml");
        ianaTypes.add("application/ccxml+xml");
        ianaTypes.add("application/cdmi-capability");
        ianaTypes.add("application/cdmi-container");
        ianaTypes.add("application/cdmi-domain");
        ianaTypes.add("application/cdmi-object");
        ianaTypes.add("application/cdmi-queue");
        ianaTypes.add("application/cdni");
        ianaTypes.add("application/cea-2018+xml");
        ianaTypes.add("application/cellml+xml");
        ianaTypes.add("application/cfw");
        ianaTypes.add("application/clue+xml");
        ianaTypes.add("application/clue_info+xml");
        ianaTypes.add("application/cms");
        ianaTypes.add("application/cnrp+xml");
        ianaTypes.add("application/coap-group+json");
        ianaTypes.add("application/coap-payload");
        ianaTypes.add("application/commonground");
        ianaTypes.add("application/conference-info+xml");
        ianaTypes.add("application/cose");
        ianaTypes.add("application/cose-key");
        ianaTypes.add("application/cose-key-set");
        ianaTypes.add("application/cpl+xml");
        ianaTypes.add("application/csrattrs");
        ianaTypes.add("application/csta+xml");
        ianaTypes.add("application/csvm+json");
        ianaTypes.add("application/cwt");
        ianaTypes.add("application/cybercash");
        ianaTypes.add("application/dash+xml");
        ianaTypes.add("application/dashdelta");
        ianaTypes.add("application/davmount+xml");
        ianaTypes.add("application/dca-rft");
        ianaTypes.add("application/dec-dx");
        ianaTypes.add("application/dialog-info+xml");
        ianaTypes.add("application/dicom");
        ianaTypes.add("application/dicom+json");
        ianaTypes.add("application/dicom+xml");
        ianaTypes.add("application/dns");
        ianaTypes.add("application/dns+json");
        ianaTypes.add("application/dns-message");
        ianaTypes.add("application/dots+cbor");
        ianaTypes.add("application/dskpp+xml");
        ianaTypes.add("application/dssc+der");
        ianaTypes.add("application/dssc+xml");
        ianaTypes.add("application/dvcs");
        ianaTypes.add("application/ecmascript");
        ianaTypes.add("application/efi");
        ianaTypes.add("application/emotionml+xml");
        ianaTypes.add("application/encaprtp");
        ianaTypes.add("application/epp+xml");
        ianaTypes.add("application/epub+zip");
        ianaTypes.add("application/eshop");
        ianaTypes.add("application/example");
        ianaTypes.add("application/expect-ct-report+json");
        ianaTypes.add("application/fastinfoset");
        ianaTypes.add("application/fastsoap");
        ianaTypes.add("application/fdt+xml");
        ianaTypes.add("application/fhir+json");
        ianaTypes.add("application/fhir+xml");
        ianaTypes.add("application/fits");
        ianaTypes.add("application/flexfec");
        ianaTypes.add("application/font-sfnt");
        ianaTypes.add("application/font-tdpfr");
        ianaTypes.add("application/font-woff");
        ianaTypes.add("application/framework-attributes+xml");
        ianaTypes.add("application/geo+json");
        ianaTypes.add("application/geo+json-seq");
        ianaTypes.add("application/geopackage+sqlite3");
        ianaTypes.add("application/geoxacml+xml");
        ianaTypes.add("application/gltf-buffer");
        ianaTypes.add("application/gml+xml");
        ianaTypes.add("application/gzip");
        ianaTypes.add("application/held+xml");
        ianaTypes.add("application/http");
        ianaTypes.add("application/hyperstudio");
        ianaTypes.add("application/ibe-key-request+xml");
        ianaTypes.add("application/ibe-pkg-reply+xml");
        ianaTypes.add("application/ibe-pp-data");
        ianaTypes.add("application/iges");
        ianaTypes.add("application/im-iscomposing+xml");
        ianaTypes.add("application/index");
        ianaTypes.add("application/index.cmd");
        ianaTypes.add("application/index.obj");
        ianaTypes.add("application/index.response");
        ianaTypes.add("application/index.vnd");
        ianaTypes.add("application/inkml+xml");
        ianaTypes.add("application/ipfix");
        ianaTypes.add("application/ipp");
        ianaTypes.add("application/its+xml");
        ianaTypes.add("application/javascript");
        ianaTypes.add("application/jf2feed+json");
        ianaTypes.add("application/jose");
        ianaTypes.add("application/jose+json");
        ianaTypes.add("application/jrd+json");
        ianaTypes.add("application/json");
        ianaTypes.add("application/json-patch+json");
        ianaTypes.add("application/json-seq");
        ianaTypes.add("application/jwk+json");
        ianaTypes.add("application/jwk-set+json");
        ianaTypes.add("application/jwt");
        ianaTypes.add("application/kpml-request+xml");
        ianaTypes.add("application/kpml-response+xml");
        ianaTypes.add("application/ld+json");
        ianaTypes.add("application/lgr+xml");
        ianaTypes.add("application/link-format");
        ianaTypes.add("application/load-control+xml");
        ianaTypes.add("application/lost+xml");
        ianaTypes.add("application/lostsync+xml");
        ianaTypes.add("application/mac-binhex40");
        ianaTypes.add("application/macwriteii");
        ianaTypes.add("application/mads+xml");
        ianaTypes.add("application/marc");
        ianaTypes.add("application/marcxml+xml");
        ianaTypes.add("application/mathematica");
        ianaTypes.add("application/mbms-associated-procedure-description+xml");
        ianaTypes.add("application/mbms-deregister+xml");
        ianaTypes.add("application/mbms-envelope+xml");
        ianaTypes.add("application/mbms-msk+xml");
        ianaTypes.add("application/mbms-msk-response+xml");
        ianaTypes.add("application/mbms-protection-description+xml");
        ianaTypes.add("application/mbms-reception-report+xml");
        ianaTypes.add("application/mbms-register+xml");
        ianaTypes.add("application/mbms-register-response+xml");
        ianaTypes.add("application/mbms-schedule+xml");
        ianaTypes.add("application/mbms-user-service-description+xml");
        ianaTypes.add("application/mbox");
        ianaTypes.add("application/media-policy-dataset+xml");
        ianaTypes.add("application/media_control+xml");
        ianaTypes.add("application/mediaservercontrol+xml");
        ianaTypes.add("application/merge-patch+json");
        ianaTypes.add("application/metalink4+xml");
        ianaTypes.add("application/mets+xml");
        ianaTypes.add("application/mikey");
        ianaTypes.add("application/mipc");
        ianaTypes.add("application/mmt-aei+xml");
        ianaTypes.add("application/mmt-usd+xml");
        ianaTypes.add("application/mods+xml");
        ianaTypes.add("application/moss-keys");
        ianaTypes.add("application/moss-signature");
        ianaTypes.add("application/mosskey-data");
        ianaTypes.add("application/mosskey-request");
        ianaTypes.add("application/mp21");
        ianaTypes.add("application/mp4");
        ianaTypes.add("application/mpeg4-generic");
        ianaTypes.add("application/mpeg4-iod");
        ianaTypes.add("application/mpeg4-iod-xmt");
        ianaTypes.add("application/mrb-consumer+xml");
        ianaTypes.add("application/mrb-publish+xml");
        ianaTypes.add("application/msc-ivr+xml");
        ianaTypes.add("application/msc-mixer+xml");
        ianaTypes.add("application/msword");
        ianaTypes.add("application/mud+json");
        ianaTypes.add("application/multipart-core");
        ianaTypes.add("application/mxf");
        ianaTypes.add("application/n-quads");
        ianaTypes.add("application/n-triples");
        ianaTypes.add("application/nasdata");
        ianaTypes.add("application/news-checkgroups");
        ianaTypes.add("application/news-groupinfo");
        ianaTypes.add("application/news-transmission");
        ianaTypes.add("application/nlsml+xml");
        ianaTypes.add("application/node");
        ianaTypes.add("application/nss");
        ianaTypes.add("application/ocsp-request");
        ianaTypes.add("application/ocsp-response");
        ianaTypes.add("application/octet-stream");
        ianaTypes.add("application/odm+xml");
        ianaTypes.add("application/oebps-package+xml");
        ianaTypes.add("application/ogg");
        ianaTypes.add("application/oscore");
        ianaTypes.add("application/oxps");
        ianaTypes.add("application/p2p-overlay+xml");
        ianaTypes.add("application/passport");
        ianaTypes.add("application/patch-ops-error+xml");
        ianaTypes.add("application/pdf");
        ianaTypes.add("application/pem-certificate-chain");
        ianaTypes.add("application/pgp-encrypted");
        ianaTypes.add("application/pgp-signature");
        ianaTypes.add("application/pidf+xml");
        ianaTypes.add("application/pidf-diff+xml");
        ianaTypes.add("application/pkcs10");
        ianaTypes.add("application/pkcs12");
        ianaTypes.add("application/pkcs7-mime");
        ianaTypes.add("application/pkcs7-signature");
        ianaTypes.add("application/pkcs8");
        ianaTypes.add("application/pkcs8-encrypted");
        ianaTypes.add("application/pkix-attr-cert");
        ianaTypes.add("application/pkix-cert");
        ianaTypes.add("application/pkix-crl");
        ianaTypes.add("application/pkix-pkipath");
        ianaTypes.add("application/pkixcmp");
        ianaTypes.add("application/pls+xml");
        ianaTypes.add("application/poc-settings+xml");
        ianaTypes.add("application/postscript");
        ianaTypes.add("application/ppsp-tracker+json");
        ianaTypes.add("application/problem+json");
        ianaTypes.add("application/problem+xml");
        ianaTypes.add("application/provenance+xml");
        ianaTypes.add("application/prs.alvestrand.titrax-sheet");
        ianaTypes.add("application/prs.cww");
        ianaTypes.add("application/prs.hpub+zip");
        ianaTypes.add("application/prs.nprend");
        ianaTypes.add("application/prs.plucker");
        ianaTypes.add("application/prs.rdf-xml-crypt");
        ianaTypes.add("application/prs.xsf+xml");
        ianaTypes.add("application/pskc+xml");
        ianaTypes.add("application/raptorfec");
        ianaTypes.add("application/rdap+json");
        ianaTypes.add("application/rdf+xml");
        ianaTypes.add("application/reginfo+xml");
        ianaTypes.add("application/relax-ng-compact-syntax");
        ianaTypes.add("application/remote-printing");
        ianaTypes.add("application/reputon+json");
        ianaTypes.add("application/resource-lists+xml");
        ianaTypes.add("application/resource-lists-diff+xml");
        ianaTypes.add("application/rfc+xml");
        ianaTypes.add("application/riscos");
        ianaTypes.add("application/rlmi+xml");
        ianaTypes.add("application/rls-services+xml");
        ianaTypes.add("application/route-apd+xml");
        ianaTypes.add("application/route-s-tsid+xml");
        ianaTypes.add("application/route-usd+xml");
        ianaTypes.add("application/rpki-ghostbusters");
        ianaTypes.add("application/rpki-manifest");
        ianaTypes.add("application/rpki-publication");
        ianaTypes.add("application/rpki-roa");
        ianaTypes.add("application/rpki-updown");
        ianaTypes.add("application/rtf");
        ianaTypes.add("application/rtploopback");
        ianaTypes.add("application/rtx");
        ianaTypes.add("application/samlassertion+xml");
        ianaTypes.add("application/samlmetadata+xml");
        ianaTypes.add("application/sbml+xml");
        ianaTypes.add("application/scaip+xml");
        ianaTypes.add("application/scim+json");
        ianaTypes.add("application/scvp-cv-request");
        ianaTypes.add("application/scvp-cv-response");
        ianaTypes.add("application/scvp-vp-request");
        ianaTypes.add("application/scvp-vp-response");
        ianaTypes.add("application/sdp");
        ianaTypes.add("application/secevent+jwt");
        ianaTypes.add("application/senml+cbor");
        ianaTypes.add("application/senml+json");
        ianaTypes.add("application/senml+xml");
        ianaTypes.add("application/senml-exi");
        ianaTypes.add("application/sensml+cbor");
        ianaTypes.add("application/sensml+json");
        ianaTypes.add("application/sensml+xml");
        ianaTypes.add("application/sensml-exi");
        ianaTypes.add("application/sep+xml");
        ianaTypes.add("application/sep-exi");
        ianaTypes.add("application/session-info");
        ianaTypes.add("application/set-payment");
        ianaTypes.add("application/set-payment-initiation");
        ianaTypes.add("application/set-registration");
        ianaTypes.add("application/set-registration-initiation");
        ianaTypes.add("application/sgml-open-catalog");
        ianaTypes.add("application/shf+xml");
        ianaTypes.add("application/sieve");
        ianaTypes.add("application/simple-filter+xml");
        ianaTypes.add("application/simple-message-summary");
        ianaTypes.add("application/simpleSymbolContainer");
        ianaTypes.add("application/sipc");
        ianaTypes.add("application/slate");
        ianaTypes.add("application/smil");
        ianaTypes.add("application/smil+xml");
        ianaTypes.add("application/smpte336m");
        ianaTypes.add("application/soap+fastinfoset");
        ianaTypes.add("application/soap+xml");
        ianaTypes.add("application/spirits-event+xml");
        ianaTypes.add("application/sql");
        ianaTypes.add("application/srgs");
        ianaTypes.add("application/srgs+xml");
        ianaTypes.add("application/sru+xml");
        ianaTypes.add("application/ssml+xml");
        ianaTypes.add("application/stix+json");
        ianaTypes.add("application/swid+xml");
        ianaTypes.add("application/tamp-apex-update");
        ianaTypes.add("application/tamp-apex-update-confirm");
        ianaTypes.add("application/tamp-community-update");
        ianaTypes.add("application/tamp-community-update-confirm");
        ianaTypes.add("application/tamp-error");
        ianaTypes.add("application/tamp-sequence-adjust");
        ianaTypes.add("application/tamp-sequence-adjust-confirm");
        ianaTypes.add("application/tamp-status-query");
        ianaTypes.add("application/tamp-status-response");
        ianaTypes.add("application/tamp-update");
        ianaTypes.add("application/tamp-update-confirm");
        ianaTypes.add("application/taxii+json");
        ianaTypes.add("application/tei+xml");
        ianaTypes.add("application/thraud+xml");
        ianaTypes.add("application/timestamp-query");
        ianaTypes.add("application/timestamp-reply");
        ianaTypes.add("application/timestamped-data");
        ianaTypes.add("application/tlsrpt+gzip");
        ianaTypes.add("application/tlsrpt+json");
        ianaTypes.add("application/tnauthlist");
        ianaTypes.add("application/trickle-ice-sdpfrag");
        ianaTypes.add("application/trig");
        ianaTypes.add("application/ttml+xml");
        ianaTypes.add("application/tve-trigger");
        ianaTypes.add("application/tzif");
        ianaTypes.add("application/tzif-leap");
        ianaTypes.add("application/ulpfec");
        ianaTypes.add("application/urc-grpsheet+xml");
        ianaTypes.add("application/urc-ressheet+xml");
        ianaTypes.add("application/urc-targetdesc+xml");
        ianaTypes.add("application/urc-uisocketdesc+xml");
        ianaTypes.add("application/vcard+json");
        ianaTypes.add("application/vcard+xml");
        ianaTypes.add("application/vemmi");
        ianaTypes.add("application/vnd.1000minds.decision-model+xml");
        ianaTypes.add("application/vnd.3M.Post-it-Notes");
        ianaTypes.add("application/vnd.3gpp-prose+xml");
        ianaTypes.add("application/vnd.3gpp-prose-pc3ch+xml");
        ianaTypes.add("application/vnd.3gpp-v2x-local-service-information");
        ianaTypes.add("application/vnd.3gpp.GMOP+xml");
        ianaTypes.add("application/vnd.3gpp.SRVCC-info+xml");
        ianaTypes.add("application/vnd.3gpp.access-transfer-events+xml");
        ianaTypes.add("application/vnd.3gpp.bsf+xml");
        ianaTypes.add("application/vnd.3gpp.mc-signalling-ear");
        ianaTypes.add("application/vnd.3gpp.mcdata-affiliation-command+xml");
        ianaTypes.add("application/vnd.3gpp.mcdata-info+xml");
        ianaTypes.add("application/vnd.3gpp.mcdata-payload");
        ianaTypes.add("application/vnd.3gpp.mcdata-service-config+xml");
        ianaTypes.add("application/vnd.3gpp.mcdata-signalling");
        ianaTypes.add("application/vnd.3gpp.mcdata-ue-config+xml");
        ianaTypes.add("application/vnd.3gpp.mcdata-user-profile+xml");
        ianaTypes.add("application/vnd.3gpp.mcptt-affiliation-command+xml");
        ianaTypes.add("application/vnd.3gpp.mcptt-floor-request+xml");
        ianaTypes.add("application/vnd.3gpp.mcptt-info+xml");
        ianaTypes.add("application/vnd.3gpp.mcptt-location-info+xml");
        ianaTypes.add("application/vnd.3gpp.mcptt-mbms-usage-info+xml");
        ianaTypes.add("application/vnd.3gpp.mcptt-service-config+xml");
        ianaTypes.add("application/vnd.3gpp.mcptt-signed+xml");
        ianaTypes.add("application/vnd.3gpp.mcptt-ue-config+xml");
        ianaTypes.add("application/vnd.3gpp.mcptt-ue-init-config+xml");
        ianaTypes.add("application/vnd.3gpp.mcptt-user-profile+xml");
        ianaTypes.add("application/vnd.3gpp.mcvideo-affiliation-command+xml");
        ianaTypes.add("application/vnd.3gpp.mcvideo-affiliation-info+xml");
        ianaTypes.add("application/vnd.3gpp.mcvideo-info+xml");
        ianaTypes.add("application/vnd.3gpp.mcvideo-location-info+xml");
        ianaTypes.add("application/vnd.3gpp.mcvideo-mbms-usage-info+xml");
        ianaTypes.add("application/vnd.3gpp.mcvideo-service-config+xml");
        ianaTypes.add("application/vnd.3gpp.mcvideo-transmission-request+xml");
        ianaTypes.add("application/vnd.3gpp.mcvideo-ue-config+xml");
        ianaTypes.add("application/vnd.3gpp.mcvideo-user-profile+xml");
        ianaTypes.add("application/vnd.3gpp.mid-call+xml");
        ianaTypes.add("application/vnd.3gpp.pic-bw-large");
        ianaTypes.add("application/vnd.3gpp.pic-bw-small");
        ianaTypes.add("application/vnd.3gpp.pic-bw-var");
        ianaTypes.add("application/vnd.3gpp.sms");
        ianaTypes.add("application/vnd.3gpp.sms+xml");
        ianaTypes.add("application/vnd.3gpp.srvcc-ext+xml");
        ianaTypes.add("application/vnd.3gpp.state-and-event-info+xml");
        ianaTypes.add("application/vnd.3gpp.ussd+xml");
        ianaTypes.add("application/vnd.3gpp2.bcmcsinfo+xml");
        ianaTypes.add("application/vnd.3gpp2.sms");
        ianaTypes.add("application/vnd.3gpp2.tcap");
        ianaTypes.add("application/vnd.3lightssoftware.imagescal");
        ianaTypes.add("application/vnd.FloGraphIt");
        ianaTypes.add("application/vnd.HandHeld-Entertainment+xml");
        ianaTypes.add("application/vnd.Kinar");
        ianaTypes.add("application/vnd.MFER");
        ianaTypes.add("application/vnd.Mobius.DAF");
        ianaTypes.add("application/vnd.Mobius.DIS");
        ianaTypes.add("application/vnd.Mobius.MBK");
        ianaTypes.add("application/vnd.Mobius.MQY");
        ianaTypes.add("application/vnd.Mobius.MSL");
        ianaTypes.add("application/vnd.Mobius.PLC");
        ianaTypes.add("application/vnd.Mobius.TXF");
        ianaTypes.add("application/vnd.Quark.QuarkXPress");
        ianaTypes.add("application/vnd.RenLearn.rlprint");
        ianaTypes.add("application/vnd.SimTech-MindMapper");
        ianaTypes.add("application/vnd.accpac.simply.aso");
        ianaTypes.add("application/vnd.accpac.simply.imp");
        ianaTypes.add("application/vnd.acucobol");
        ianaTypes.add("application/vnd.acucorp");
        ianaTypes.add("application/vnd.adobe.flash.movie");
        ianaTypes.add("application/vnd.adobe.formscentral.fcdt");
        ianaTypes.add("application/vnd.adobe.fxp");
        ianaTypes.add("application/vnd.adobe.partial-upload");
        ianaTypes.add("application/vnd.adobe.xdp+xml");
        ianaTypes.add("application/vnd.adobe.xfdf");
        ianaTypes.add("application/vnd.aether.imp");
        ianaTypes.add("application/vnd.afpc.afplinedata");
        ianaTypes.add("application/vnd.afpc.afplinedata-pagedef");
        ianaTypes.add("application/vnd.afpc.foca-charset");
        ianaTypes.add("application/vnd.afpc.foca-codedfont");
        ianaTypes.add("application/vnd.afpc.foca-codepage");
        ianaTypes.add("application/vnd.afpc.modca");
        ianaTypes.add("application/vnd.afpc.modca-formdef");
        ianaTypes.add("application/vnd.afpc.modca-mediummap");
        ianaTypes.add("application/vnd.afpc.modca-objectcontainer");
        ianaTypes.add("application/vnd.afpc.modca-overlay");
        ianaTypes.add("application/vnd.afpc.modca-pagesegment");
        ianaTypes.add("application/vnd.ah-barcode");
        ianaTypes.add("application/vnd.ahead.space");
        ianaTypes.add("application/vnd.airzip.filesecure.azf");
        ianaTypes.add("application/vnd.airzip.filesecure.azs");
        ianaTypes.add("application/vnd.amadeus+json");
        ianaTypes.add("application/vnd.amazon.mobi8-ebook");
        ianaTypes.add("application/vnd.americandynamics.acc");
        ianaTypes.add("application/vnd.amiga.ami");
        ianaTypes.add("application/vnd.amundsen.maze+xml");
        ianaTypes.add("application/vnd.android.ota");
        ianaTypes.add("application/vnd.anki");
        ianaTypes.add("application/vnd.anser-web-certificate-issue-initiation");
        ianaTypes.add("application/vnd.antix.game-component");
        ianaTypes.add("application/vnd.apache.thrift.binary");
        ianaTypes.add("application/vnd.apache.thrift.compact");
        ianaTypes.add("application/vnd.apache.thrift.json");
        ianaTypes.add("application/vnd.api+json");
        ianaTypes.add("application/vnd.aplextor.warrp+json");
        ianaTypes.add("application/vnd.apothekende.reservation+json");
        ianaTypes.add("application/vnd.apple.installer+xml");
        ianaTypes.add("application/vnd.apple.keynote");
        ianaTypes.add("application/vnd.apple.mpegurl");
        ianaTypes.add("application/vnd.apple.numbers");
        ianaTypes.add("application/vnd.apple.pages");
        ianaTypes.add("application/vnd.arastra.swi");
        ianaTypes.add("application/vnd.aristanetworks.swi");
        ianaTypes.add("application/vnd.artisan+json");
        ianaTypes.add("application/vnd.artsquare");
        ianaTypes.add("application/vnd.astraea-software.iota");
        ianaTypes.add("application/vnd.audiograph");
        ianaTypes.add("application/vnd.autopackage");
        ianaTypes.add("application/vnd.avalon+json");
        ianaTypes.add("application/vnd.avistar+xml");
        ianaTypes.add("application/vnd.balsamiq.bmml+xml");
        ianaTypes.add("application/vnd.balsamiq.bmpr");
        ianaTypes.add("application/vnd.banana-accounting");
        ianaTypes.add("application/vnd.bbf.usp.error");
        ianaTypes.add("application/vnd.bbf.usp.msg");
        ianaTypes.add("application/vnd.bbf.usp.msg+json");
        ianaTypes.add("application/vnd.bekitzur-stech+json");
        ianaTypes.add("application/vnd.bint.med-content");
        ianaTypes.add("application/vnd.biopax.rdf+xml");
        ianaTypes.add("application/vnd.blink-idb-value-wrapper");
        ianaTypes.add("application/vnd.blueice.multipass");
        ianaTypes.add("application/vnd.bluetooth.ep.oob");
        ianaTypes.add("application/vnd.bluetooth.le.oob");
        ianaTypes.add("application/vnd.bmi");
        ianaTypes.add("application/vnd.bpf");
        ianaTypes.add("application/vnd.bpf3");
        ianaTypes.add("application/vnd.businessobjects");
        ianaTypes.add("application/vnd.byu.uapi+json");
        ianaTypes.add("application/vnd.cab-jscript");
        ianaTypes.add("application/vnd.canon-cpdl");
        ianaTypes.add("application/vnd.canon-lips");
        ianaTypes.add("application/vnd.capasystems-pg+json");
        ianaTypes.add("application/vnd.cendio.thinlinc.clientconf");
        ianaTypes.add("application/vnd.century-systems.tcp_stream");
        ianaTypes.add("application/vnd.chemdraw+xml");
        ianaTypes.add("application/vnd.chess-pgn");
        ianaTypes.add("application/vnd.chipnuts.karaoke-mmd");
        ianaTypes.add("application/vnd.ciedi");
        ianaTypes.add("application/vnd.cinderella");
        ianaTypes.add("application/vnd.cirpack.isdn-ext");
        ianaTypes.add("application/vnd.citationstyles.style+xml");
        ianaTypes.add("application/vnd.claymore");
        ianaTypes.add("application/vnd.cloanto.rp9");
        ianaTypes.add("application/vnd.clonk.c4group");
        ianaTypes.add("application/vnd.cluetrust.cartomobile-config");
        ianaTypes.add("application/vnd.cluetrust.cartomobile-config-pkg");
        ianaTypes.add("application/vnd.coffeescript");
        ianaTypes.add("application/vnd.collabio.xodocuments.document");
        ianaTypes.add("application/vnd.collabio.xodocuments.document-template");
        ianaTypes.add("application/vnd.collabio.xodocuments.presentation");
        ianaTypes.add("application/vnd.collabio.xodocuments.presentation-template");
        ianaTypes.add("application/vnd.collabio.xodocuments.spreadsheet");
        ianaTypes.add("application/vnd.collabio.xodocuments.spreadsheet-template");
        ianaTypes.add("application/vnd.collection+json");
        ianaTypes.add("application/vnd.collection.doc+json");
        ianaTypes.add("application/vnd.collection.next+json");
        ianaTypes.add("application/vnd.comicbook+zip");
        ianaTypes.add("application/vnd.comicbook-rar");
        ianaTypes.add("application/vnd.commerce-battelle");
        ianaTypes.add("application/vnd.commonspace");
        ianaTypes.add("application/vnd.contact.cmsg");
        ianaTypes.add("application/vnd.coreos.ignition+json");
        ianaTypes.add("application/vnd.cosmocaller");
        ianaTypes.add("application/vnd.crick.clicker");
        ianaTypes.add("application/vnd.crick.clicker.keyboard");
        ianaTypes.add("application/vnd.crick.clicker.palette");
        ianaTypes.add("application/vnd.crick.clicker.template");
        ianaTypes.add("application/vnd.crick.clicker.wordbank");
        ianaTypes.add("application/vnd.criticaltools.wbs+xml");
        ianaTypes.add("application/vnd.cryptii.pipe+json");
        ianaTypes.add("application/vnd.crypto-shade-file");
        ianaTypes.add("application/vnd.ctc-posml");
        ianaTypes.add("application/vnd.ctct.ws+xml");
        ianaTypes.add("application/vnd.cups-pdf");
        ianaTypes.add("application/vnd.cups-postscript");
        ianaTypes.add("application/vnd.cups-ppd");
        ianaTypes.add("application/vnd.cups-raster");
        ianaTypes.add("application/vnd.cups-raw");
        ianaTypes.add("application/vnd.curl");
        ianaTypes.add("application/vnd.cyan.dean.root+xml");
        ianaTypes.add("application/vnd.cybank");
        ianaTypes.add("application/vnd.d2l.coursepackage1p0+zip");
        ianaTypes.add("application/vnd.dart");
        ianaTypes.add("application/vnd.data-vision.rdz");
        ianaTypes.add("application/vnd.datapackage+json");
        ianaTypes.add("application/vnd.dataresource+json");
        ianaTypes.add("application/vnd.debian.binary-package");
        ianaTypes.add("application/vnd.dece.data");
        ianaTypes.add("application/vnd.dece.ttml+xml");
        ianaTypes.add("application/vnd.dece.unspecified");
        ianaTypes.add("application/vnd.dece.zip");
        ianaTypes.add("application/vnd.denovo.fcselayout-link");
        ianaTypes.add("application/vnd.desmume.movie");
        ianaTypes.add("application/vnd.dir-bi.plate-dl-nosuffix");
        ianaTypes.add("application/vnd.dm.delegation+xml");
        ianaTypes.add("application/vnd.dna");
        ianaTypes.add("application/vnd.document+json");
        ianaTypes.add("application/vnd.dolby.mobile.1");
        ianaTypes.add("application/vnd.dolby.mobile.2");
        ianaTypes.add("application/vnd.doremir.scorecloud-binary-document");
        ianaTypes.add("application/vnd.dpgraph");
        ianaTypes.add("application/vnd.dreamfactory");
        ianaTypes.add("application/vnd.drive+json");
        ianaTypes.add("application/vnd.dtg.local");
        ianaTypes.add("application/vnd.dtg.local.flash");
        ianaTypes.add("application/vnd.dtg.local.html");
        ianaTypes.add("application/vnd.dvb.ait");
        ianaTypes.add("application/vnd.dvb.dvbisl+xml");
        ianaTypes.add("application/vnd.dvb.dvbj");
        ianaTypes.add("application/vnd.dvb.esgcontainer");
        ianaTypes.add("application/vnd.dvb.ipdcdftnotifaccess");
        ianaTypes.add("application/vnd.dvb.ipdcesgaccess");
        ianaTypes.add("application/vnd.dvb.ipdcesgaccess2");
        ianaTypes.add("application/vnd.dvb.ipdcesgpdd");
        ianaTypes.add("application/vnd.dvb.ipdcroaming");
        ianaTypes.add("application/vnd.dvb.iptv.alfec-base");
        ianaTypes.add("application/vnd.dvb.iptv.alfec-enhancement");
        ianaTypes.add("application/vnd.dvb.notif-aggregate-root+xml");
        ianaTypes.add("application/vnd.dvb.notif-container+xml");
        ianaTypes.add("application/vnd.dvb.notif-generic+xml");
        ianaTypes.add("application/vnd.dvb.notif-ia-msglist+xml");
        ianaTypes.add("application/vnd.dvb.notif-ia-registration-request+xml");
        ianaTypes.add("application/vnd.dvb.notif-ia-registration-response+xml");
        ianaTypes.add("application/vnd.dvb.notif-init+xml");
        ianaTypes.add("application/vnd.dvb.pfr");
        ianaTypes.add("application/vnd.dvb.service");
        ianaTypes.add("application/vnd.dxr");
        ianaTypes.add("application/vnd.dynageo");
        ianaTypes.add("application/vnd.dzr");
        ianaTypes.add("application/vnd.easykaraoke.cdgdownload");
        ianaTypes.add("application/vnd.ecdis-update");
        ianaTypes.add("application/vnd.ecip.rlp");
        ianaTypes.add("application/vnd.ecowin.chart");
        ianaTypes.add("application/vnd.ecowin.filerequest");
        ianaTypes.add("application/vnd.ecowin.fileupdate");
        ianaTypes.add("application/vnd.ecowin.series");
        ianaTypes.add("application/vnd.ecowin.seriesrequest");
        ianaTypes.add("application/vnd.ecowin.seriesupdate");
        ianaTypes.add("application/vnd.efi.img");
        ianaTypes.add("application/vnd.efi.iso");
        ianaTypes.add("application/vnd.emclient.accessrequest+xml");
        ianaTypes.add("application/vnd.enliven");
        ianaTypes.add("application/vnd.enphase.envoy");
        ianaTypes.add("application/vnd.eprints.data+xml");
        ianaTypes.add("application/vnd.epson.esf");
        ianaTypes.add("application/vnd.epson.msf");
        ianaTypes.add("application/vnd.epson.quickanime");
        ianaTypes.add("application/vnd.epson.salt");
        ianaTypes.add("application/vnd.epson.ssf");
        ianaTypes.add("application/vnd.ericsson.quickcall");
        ianaTypes.add("application/vnd.espass-espass+zip");
        ianaTypes.add("application/vnd.eszigno3+xml");
        ianaTypes.add("application/vnd.etsi.aoc+xml");
        ianaTypes.add("application/vnd.etsi.asic-e+zip");
        ianaTypes.add("application/vnd.etsi.asic-s+zip");
        ianaTypes.add("application/vnd.etsi.cug+xml");
        ianaTypes.add("application/vnd.etsi.iptvcommand+xml");
        ianaTypes.add("application/vnd.etsi.iptvdiscovery+xml");
        ianaTypes.add("application/vnd.etsi.iptvprofile+xml");
        ianaTypes.add("application/vnd.etsi.iptvsad-bc+xml");
        ianaTypes.add("application/vnd.etsi.iptvsad-cod+xml");
        ianaTypes.add("application/vnd.etsi.iptvsad-npvr+xml");
        ianaTypes.add("application/vnd.etsi.iptvservice+xml");
        ianaTypes.add("application/vnd.etsi.iptvsync+xml");
        ianaTypes.add("application/vnd.etsi.iptvueprofile+xml");
        ianaTypes.add("application/vnd.etsi.mcid+xml");
        ianaTypes.add("application/vnd.etsi.mheg5");
        ianaTypes.add("application/vnd.etsi.overload-control-policy-dataset+xml");
        ianaTypes.add("application/vnd.etsi.pstn+xml");
        ianaTypes.add("application/vnd.etsi.sci+xml");
        ianaTypes.add("application/vnd.etsi.simservs+xml");
        ianaTypes.add("application/vnd.etsi.timestamp-token");
        ianaTypes.add("application/vnd.etsi.tsl+xml");
        ianaTypes.add("application/vnd.etsi.tsl.der");
        ianaTypes.add("application/vnd.eudora.data");
        ianaTypes.add("application/vnd.evolv.ecig.profile");
        ianaTypes.add("application/vnd.evolv.ecig.settings");
        ianaTypes.add("application/vnd.evolv.ecig.theme");
        ianaTypes.add("application/vnd.exstream-empower+zip");
        ianaTypes.add("application/vnd.exstream-package");
        ianaTypes.add("application/vnd.ezpix-album");
        ianaTypes.add("application/vnd.ezpix-package");
        ianaTypes.add("application/vnd.f-secure.mobile");
        ianaTypes.add("application/vnd.fastcopy-disk-image");
        ianaTypes.add("application/vnd.fdf");
        ianaTypes.add("application/vnd.fdsn.mseed");
        ianaTypes.add("application/vnd.fdsn.seed");
        ianaTypes.add("application/vnd.ffsns");
        ianaTypes.add("application/vnd.ficlab.flb+zip");
        ianaTypes.add("application/vnd.filmit.zfc");
        ianaTypes.add("application/vnd.fints");
        ianaTypes.add("application/vnd.firemonkeys.cloudcell");
        ianaTypes.add("application/vnd.fluxtime.clip");
        ianaTypes.add("application/vnd.font-fontforge-sfd");
        ianaTypes.add("application/vnd.framemaker");
        ianaTypes.add("application/vnd.frogans.fnc");
        ianaTypes.add("application/vnd.frogans.ltf");
        ianaTypes.add("application/vnd.fsc.weblaunch");
        ianaTypes.add("application/vnd.fujitsu.oasys");
        ianaTypes.add("application/vnd.fujitsu.oasys2");
        ianaTypes.add("application/vnd.fujitsu.oasys3");
        ianaTypes.add("application/vnd.fujitsu.oasysgp");
        ianaTypes.add("application/vnd.fujitsu.oasysprs");
        ianaTypes.add("application/vnd.fujixerox.ART-EX");
        ianaTypes.add("application/vnd.fujixerox.ART4");
        ianaTypes.add("application/vnd.fujixerox.HBPL");
        ianaTypes.add("application/vnd.fujixerox.ddd");
        ianaTypes.add("application/vnd.fujixerox.docuworks");
        ianaTypes.add("application/vnd.fujixerox.docuworks.binder");
        ianaTypes.add("application/vnd.fujixerox.docuworks.container");
        ianaTypes.add("application/vnd.fut-misnet");
        ianaTypes.add("application/vnd.futoin+cbor");
        ianaTypes.add("application/vnd.futoin+json");
        ianaTypes.add("application/vnd.fuzzysheet");
        ianaTypes.add("application/vnd.genomatix.tuxedo");
        ianaTypes.add("application/vnd.gentics.grd+json");
        ianaTypes.add("application/vnd.geo+json");
        ianaTypes.add("application/vnd.geocube+xml");
        ianaTypes.add("application/vnd.geogebra.file");
        ianaTypes.add("application/vnd.geogebra.tool");
        ianaTypes.add("application/vnd.geometry-explorer");
        ianaTypes.add("application/vnd.geonext");
        ianaTypes.add("application/vnd.geoplan");
        ianaTypes.add("application/vnd.geospace");
        ianaTypes.add("application/vnd.gerber");
        ianaTypes.add("application/vnd.globalplatform.card-content-mgt");
        ianaTypes.add("application/vnd.globalplatform.card-content-mgt-response");
        ianaTypes.add("application/vnd.gmx");
        ianaTypes.add("application/vnd.google-earth.kml+xml");
        ianaTypes.add("application/vnd.google-earth.kmz");
        ianaTypes.add("application/vnd.gov.sk.e-form+xml");
        ianaTypes.add("application/vnd.gov.sk.e-form+zip");
        ianaTypes.add("application/vnd.gov.sk.xmldatacontainer+xml");
        ianaTypes.add("application/vnd.grafeq");
        ianaTypes.add("application/vnd.gridmp");
        ianaTypes.add("application/vnd.groove-account");
        ianaTypes.add("application/vnd.groove-help");
        ianaTypes.add("application/vnd.groove-identity-message");
        ianaTypes.add("application/vnd.groove-injector");
        ianaTypes.add("application/vnd.groove-tool-message");
        ianaTypes.add("application/vnd.groove-tool-template");
        ianaTypes.add("application/vnd.groove-vcard");
        ianaTypes.add("application/vnd.hal+json");
        ianaTypes.add("application/vnd.hal+xml");
        ianaTypes.add("application/vnd.hbci");
        ianaTypes.add("application/vnd.hc+json");
        ianaTypes.add("application/vnd.hcl-bireports");
        ianaTypes.add("application/vnd.hdt");
        ianaTypes.add("application/vnd.heroku+json");
        ianaTypes.add("application/vnd.hhe.lesson-player");
        ianaTypes.add("application/vnd.hp-HPGL");
        ianaTypes.add("application/vnd.hp-PCL");
        ianaTypes.add("application/vnd.hp-PCLXL");
        ianaTypes.add("application/vnd.hp-hpid");
        ianaTypes.add("application/vnd.hp-hps");
        ianaTypes.add("application/vnd.hp-jlyt");
        ianaTypes.add("application/vnd.httphone");
        ianaTypes.add("application/vnd.hydrostatix.sof-data");
        ianaTypes.add("application/vnd.hyper+json");
        ianaTypes.add("application/vnd.hyper-item+json");
        ianaTypes.add("application/vnd.hyperdrive+json");
        ianaTypes.add("application/vnd.hzn-3d-crossword");
        ianaTypes.add("application/vnd.ibm.MiniPay");
        ianaTypes.add("application/vnd.ibm.afplinedata");
        ianaTypes.add("application/vnd.ibm.electronic-media");
        ianaTypes.add("application/vnd.ibm.modcap");
        ianaTypes.add("application/vnd.ibm.rights-management");
        ianaTypes.add("application/vnd.ibm.secure-container");
        ianaTypes.add("application/vnd.iccprofile");
        ianaTypes.add("application/vnd.ieee.1905");
        ianaTypes.add("application/vnd.igloader");
        ianaTypes.add("application/vnd.imagemeter.folder+zip");
        ianaTypes.add("application/vnd.imagemeter.image+zip");
        ianaTypes.add("application/vnd.immervision-ivp");
        ianaTypes.add("application/vnd.immervision-ivu");
        ianaTypes.add("application/vnd.ims.imsccv1p1");
        ianaTypes.add("application/vnd.ims.imsccv1p2");
        ianaTypes.add("application/vnd.ims.imsccv1p3");
        ianaTypes.add("application/vnd.ims.lis.v2.result+json");
        ianaTypes.add("application/vnd.ims.lti.v2.toolconsumerprofile+json");
        ianaTypes.add("application/vnd.ims.lti.v2.toolproxy+json");
        ianaTypes.add("application/vnd.ims.lti.v2.toolproxy.id+json");
        ianaTypes.add("application/vnd.ims.lti.v2.toolsettings+json");
        ianaTypes.add("application/vnd.ims.lti.v2.toolsettings.simple+json");
        ianaTypes.add("application/vnd.informedcontrol.rms+xml");
        ianaTypes.add("application/vnd.informix-visionary");
        ianaTypes.add("application/vnd.infotech.project");
        ianaTypes.add("application/vnd.infotech.project+xml");
        ianaTypes.add("application/vnd.innopath.wamp.notification");
        ianaTypes.add("application/vnd.insors.igm");
        ianaTypes.add("application/vnd.intercon.formnet");
        ianaTypes.add("application/vnd.intergeo");
        ianaTypes.add("application/vnd.intertrust.digibox");
        ianaTypes.add("application/vnd.intertrust.nncp");
        ianaTypes.add("application/vnd.intu.qbo");
        ianaTypes.add("application/vnd.intu.qfx");
        ianaTypes.add("application/vnd.iptc.g2.catalogitem+xml");
        ianaTypes.add("application/vnd.iptc.g2.conceptitem+xml");
        ianaTypes.add("application/vnd.iptc.g2.knowledgeitem+xml");
        ianaTypes.add("application/vnd.iptc.g2.newsitem+xml");
        ianaTypes.add("application/vnd.iptc.g2.newsmessage+xml");
        ianaTypes.add("application/vnd.iptc.g2.packageitem+xml");
        ianaTypes.add("application/vnd.iptc.g2.planningitem+xml");
        ianaTypes.add("application/vnd.ipunplugged.rcprofile");
        ianaTypes.add("application/vnd.irepository.package+xml");
        ianaTypes.add("application/vnd.is-xpr");
        ianaTypes.add("application/vnd.isac.fcs");
        ianaTypes.add("application/vnd.iso11783-10+zip");
        ianaTypes.add("application/vnd.jam");
        ianaTypes.add("application/vnd.japannet-directory-service");
        ianaTypes.add("application/vnd.japannet-jpnstore-wakeup");
        ianaTypes.add("application/vnd.japannet-payment-wakeup");
        ianaTypes.add("application/vnd.japannet-registration");
        ianaTypes.add("application/vnd.japannet-registration-wakeup");
        ianaTypes.add("application/vnd.japannet-setstore-wakeup");
        ianaTypes.add("application/vnd.japannet-verification");
        ianaTypes.add("application/vnd.japannet-verification-wakeup");
        ianaTypes.add("application/vnd.jcp.javame.midlet-rms");
        ianaTypes.add("application/vnd.jisp");
        ianaTypes.add("application/vnd.joost.joda-archive");
        ianaTypes.add("application/vnd.jsk.isdn-ngn");
        ianaTypes.add("application/vnd.kahootz");
        ianaTypes.add("application/vnd.kde.karbon");
        ianaTypes.add("application/vnd.kde.kchart");
        ianaTypes.add("application/vnd.kde.kformula");
        ianaTypes.add("application/vnd.kde.kivio");
        ianaTypes.add("application/vnd.kde.kontour");
        ianaTypes.add("application/vnd.kde.kpresenter");
        ianaTypes.add("application/vnd.kde.kspread");
        ianaTypes.add("application/vnd.kde.kword");
        ianaTypes.add("application/vnd.kenameaapp");
        ianaTypes.add("application/vnd.kidspiration");
        ianaTypes.add("application/vnd.koan");
        ianaTypes.add("application/vnd.kodak-descriptor");
        ianaTypes.add("application/vnd.las");
        ianaTypes.add("application/vnd.las.las+json");
        ianaTypes.add("application/vnd.las.las+xml");
        ianaTypes.add("application/vnd.laszip");
        ianaTypes.add("application/vnd.leap+json");
        ianaTypes.add("application/vnd.liberty-request+xml");
        ianaTypes.add("application/vnd.llamagraphics.life-balance.desktop");
        ianaTypes.add("application/vnd.llamagraphics.life-balance.exchange+xml");
        ianaTypes.add("application/vnd.logipipe.circuit+zip");
        ianaTypes.add("application/vnd.loom");
        ianaTypes.add("application/vnd.lotus-1-2-3");
        ianaTypes.add("application/vnd.lotus-approach");
        ianaTypes.add("application/vnd.lotus-freelance");
        ianaTypes.add("application/vnd.lotus-notes");
        ianaTypes.add("application/vnd.lotus-organizer");
        ianaTypes.add("application/vnd.lotus-screencam");
        ianaTypes.add("application/vnd.lotus-wordpro");
        ianaTypes.add("application/vnd.macports.portpkg");
        ianaTypes.add("application/vnd.mapbox-vector-tile");
        ianaTypes.add("application/vnd.marlin.drm.actiontoken+xml");
        ianaTypes.add("application/vnd.marlin.drm.conftoken+xml");
        ianaTypes.add("application/vnd.marlin.drm.license+xml");
        ianaTypes.add("application/vnd.marlin.drm.mdcf");
        ianaTypes.add("application/vnd.mason+json");
        ianaTypes.add("application/vnd.maxmind.maxmind-db");
        ianaTypes.add("application/vnd.mcd");
        ianaTypes.add("application/vnd.medcalcdata");
        ianaTypes.add("application/vnd.mediastation.cdkey");
        ianaTypes.add("application/vnd.meridian-slingshot");
        ianaTypes.add("application/vnd.mfmp");
        ianaTypes.add("application/vnd.micro+json");
        ianaTypes.add("application/vnd.micrografx.flo");
        ianaTypes.add("application/vnd.micrografx.igx");
        ianaTypes.add("application/vnd.microsoft.portable-executable");
        ianaTypes.add("application/vnd.microsoft.windows.thumbnail-cache");
        ianaTypes.add("application/vnd.miele+json");
        ianaTypes.add("application/vnd.mif");
        ianaTypes.add("application/vnd.minisoft-hp3000-save");
        ianaTypes.add("application/vnd.mitsubishi.misty-guard.trustweb");
        ianaTypes.add("application/vnd.mophun.application");
        ianaTypes.add("application/vnd.mophun.certificate");
        ianaTypes.add("application/vnd.motorola.flexsuite");
        ianaTypes.add("application/vnd.motorola.flexsuite.adsi");
        ianaTypes.add("application/vnd.motorola.flexsuite.fis");
        ianaTypes.add("application/vnd.motorola.flexsuite.gotap");
        ianaTypes.add("application/vnd.motorola.flexsuite.kmr");
        ianaTypes.add("application/vnd.motorola.flexsuite.ttc");
        ianaTypes.add("application/vnd.motorola.flexsuite.wem");
        ianaTypes.add("application/vnd.motorola.iprm");
        ianaTypes.add("application/vnd.mozilla.xul+xml");
        ianaTypes.add("application/vnd.ms-3mfdocument");
        ianaTypes.add("application/vnd.ms-PrintDeviceCapabilities+xml");
        ianaTypes.add("application/vnd.ms-PrintSchemaTicket+xml");
        ianaTypes.add("application/vnd.ms-artgalry");
        ianaTypes.add("application/vnd.ms-asf");
        ianaTypes.add("application/vnd.ms-cab-compressed");
        ianaTypes.add("application/vnd.ms-excel");
        ianaTypes.add("application/vnd.ms-excel.addin.macroEnabled.12");
        ianaTypes.add("application/vnd.ms-excel.sheet.binary.macroEnabled.12");
        ianaTypes.add("application/vnd.ms-excel.sheet.macroEnabled.12");
        ianaTypes.add("application/vnd.ms-excel.template.macroEnabled.12");
        ianaTypes.add("application/vnd.ms-fontobject");
        ianaTypes.add("application/vnd.ms-htmlhelp");
        ianaTypes.add("application/vnd.ms-ims");
        ianaTypes.add("application/vnd.ms-lrm");
        ianaTypes.add("application/vnd.ms-office.activeX+xml");
        ianaTypes.add("application/vnd.ms-officetheme");
        ianaTypes.add("application/vnd.ms-playready.initiator+xml");
        ianaTypes.add("application/vnd.ms-powerpoint");
        ianaTypes.add("application/vnd.ms-powerpoint.addin.macroEnabled.12");
        ianaTypes.add("application/vnd.ms-powerpoint.presentation.macroEnabled.12");
        ianaTypes.add("application/vnd.ms-powerpoint.slide.macroEnabled.12");
        ianaTypes.add("application/vnd.ms-powerpoint.slideshow.macroEnabled.12");
        ianaTypes.add("application/vnd.ms-powerpoint.template.macroEnabled.12");
        ianaTypes.add("application/vnd.ms-project");
        ianaTypes.add("application/vnd.ms-tnef");
        ianaTypes.add("application/vnd.ms-windows.devicepairing");
        ianaTypes.add("application/vnd.ms-windows.nwprinting.oob");
        ianaTypes.add("application/vnd.ms-windows.printerpairing");
        ianaTypes.add("application/vnd.ms-windows.wsd.oob");
        ianaTypes.add("application/vnd.ms-wmdrm.lic-chlg-req");
        ianaTypes.add("application/vnd.ms-wmdrm.lic-resp");
        ianaTypes.add("application/vnd.ms-wmdrm.meter-chlg-req");
        ianaTypes.add("application/vnd.ms-wmdrm.meter-resp");
        ianaTypes.add("application/vnd.ms-word.document.macroEnabled.12");
        ianaTypes.add("application/vnd.ms-word.template.macroEnabled.12");
        ianaTypes.add("application/vnd.ms-works");
        ianaTypes.add("application/vnd.ms-wpl");
        ianaTypes.add("application/vnd.ms-xpsdocument");
        ianaTypes.add("application/vnd.msa-disk-image");
        ianaTypes.add("application/vnd.mseq");
        ianaTypes.add("application/vnd.msign");
        ianaTypes.add("application/vnd.multiad.creator");
        ianaTypes.add("application/vnd.multiad.creator.cif");
        ianaTypes.add("application/vnd.music-niff");
        ianaTypes.add("application/vnd.musician");
        ianaTypes.add("application/vnd.muvee.style");
        ianaTypes.add("application/vnd.mynfc");
        ianaTypes.add("application/vnd.ncd.control");
        ianaTypes.add("application/vnd.ncd.reference");
        ianaTypes.add("application/vnd.nearst.inv+json");
        ianaTypes.add("application/vnd.nervana");
        ianaTypes.add("application/vnd.netfpx");
        ianaTypes.add("application/vnd.neurolanguage.nlu");
        ianaTypes.add("application/vnd.nimn");
        ianaTypes.add("application/vnd.nintendo.nitro.rom");
        ianaTypes.add("application/vnd.nintendo.snes.rom");
        ianaTypes.add("application/vnd.nitf");
        ianaTypes.add("application/vnd.noblenet-directory");
        ianaTypes.add("application/vnd.noblenet-sealer");
        ianaTypes.add("application/vnd.noblenet-web");
        ianaTypes.add("application/vnd.nokia.catalogs");
        ianaTypes.add("application/vnd.nokia.conml+wbxml");
        ianaTypes.add("application/vnd.nokia.conml+xml");
        ianaTypes.add("application/vnd.nokia.iSDS-radio-presets");
        ianaTypes.add("application/vnd.nokia.iptv.config+xml");
        ianaTypes.add("application/vnd.nokia.landmark+wbxml");
        ianaTypes.add("application/vnd.nokia.landmark+xml");
        ianaTypes.add("application/vnd.nokia.landmarkcollection+xml");
        ianaTypes.add("application/vnd.nokia.n-gage.ac+xml");
        ianaTypes.add("application/vnd.nokia.n-gage.data");
        ianaTypes.add("application/vnd.nokia.n-gage.symbian.install");
        ianaTypes.add("application/vnd.nokia.ncd");
        ianaTypes.add("application/vnd.nokia.pcd+wbxml");
        ianaTypes.add("application/vnd.nokia.pcd+xml");
        ianaTypes.add("application/vnd.nokia.radio-preset");
        ianaTypes.add("application/vnd.nokia.radio-presets");
        ianaTypes.add("application/vnd.novadigm.EDM");
        ianaTypes.add("application/vnd.novadigm.EDX");
        ianaTypes.add("application/vnd.novadigm.EXT");
        ianaTypes.add("application/vnd.ntt-local.content-share");
        ianaTypes.add("application/vnd.ntt-local.file-transfer");
        ianaTypes.add("application/vnd.ntt-local.ogw_remote-access");
        ianaTypes.add("application/vnd.ntt-local.sip-ta_remote");
        ianaTypes.add("application/vnd.ntt-local.sip-ta_tcp_stream");
        ianaTypes.add("application/vnd.oasis.opendocument.chart");
        ianaTypes.add("application/vnd.oasis.opendocument.chart-template");
        ianaTypes.add("application/vnd.oasis.opendocument.database");
        ianaTypes.add("application/vnd.oasis.opendocument.formula");
        ianaTypes.add("application/vnd.oasis.opendocument.formula-template");
        ianaTypes.add("application/vnd.oasis.opendocument.graphics");
        ianaTypes.add("application/vnd.oasis.opendocument.graphics-template");
        ianaTypes.add("application/vnd.oasis.opendocument.image");
        ianaTypes.add("application/vnd.oasis.opendocument.image-template");
        ianaTypes.add("application/vnd.oasis.opendocument.presentation");
        ianaTypes.add("application/vnd.oasis.opendocument.presentation-template");
        ianaTypes.add("application/vnd.oasis.opendocument.spreadsheet");
        ianaTypes.add("application/vnd.oasis.opendocument.spreadsheet-template");
        ianaTypes.add("application/vnd.oasis.opendocument.text");
        ianaTypes.add("application/vnd.oasis.opendocument.text-master");
        ianaTypes.add("application/vnd.oasis.opendocument.text-template");
        ianaTypes.add("application/vnd.oasis.opendocument.text-web");
        ianaTypes.add("application/vnd.obn");
        ianaTypes.add("application/vnd.ocf+cbor");
        ianaTypes.add("application/vnd.oftn.l10n+json");
        ianaTypes.add("application/vnd.oipf.contentaccessdownload+xml");
        ianaTypes.add("application/vnd.oipf.contentaccessstreaming+xml");
        ianaTypes.add("application/vnd.oipf.cspg-hexbinary");
        ianaTypes.add("application/vnd.oipf.dae.svg+xml");
        ianaTypes.add("application/vnd.oipf.dae.xhtml+xml");
        ianaTypes.add("application/vnd.oipf.mippvcontrolmessage+xml");
        ianaTypes.add("application/vnd.oipf.pae.gem");
        ianaTypes.add("application/vnd.oipf.spdiscovery+xml");
        ianaTypes.add("application/vnd.oipf.spdlist+xml");
        ianaTypes.add("application/vnd.oipf.ueprofile+xml");
        ianaTypes.add("application/vnd.oipf.userprofile+xml");
        ianaTypes.add("application/vnd.olpc-sugar");
        ianaTypes.add("application/vnd.oma-scws-config");
        ianaTypes.add("application/vnd.oma-scws-http-request");
        ianaTypes.add("application/vnd.oma-scws-http-response");
        ianaTypes.add("application/vnd.oma.bcast.associated-procedure-parameter+xml");
        ianaTypes.add("application/vnd.oma.bcast.drm-trigger+xml");
        ianaTypes.add("application/vnd.oma.bcast.imd+xml");
        ianaTypes.add("application/vnd.oma.bcast.ltkm");
        ianaTypes.add("application/vnd.oma.bcast.notification+xml");
        ianaTypes.add("application/vnd.oma.bcast.provisioningtrigger");
        ianaTypes.add("application/vnd.oma.bcast.sgboot");
        ianaTypes.add("application/vnd.oma.bcast.sgdd+xml");
        ianaTypes.add("application/vnd.oma.bcast.sgdu");
        ianaTypes.add("application/vnd.oma.bcast.simple-symbol-container");
        ianaTypes.add("application/vnd.oma.bcast.smartcard-trigger+xml");
        ianaTypes.add("application/vnd.oma.bcast.sprov+xml");
        ianaTypes.add("application/vnd.oma.bcast.stkm");
        ianaTypes.add("application/vnd.oma.cab-address-book+xml");
        ianaTypes.add("application/vnd.oma.cab-feature-handler+xml");
        ianaTypes.add("application/vnd.oma.cab-pcc+xml");
        ianaTypes.add("application/vnd.oma.cab-subs-invite+xml");
        ianaTypes.add("application/vnd.oma.cab-user-prefs+xml");
        ianaTypes.add("application/vnd.oma.dcd");
        ianaTypes.add("application/vnd.oma.dcdc");
        ianaTypes.add("application/vnd.oma.dd2+xml");
        ianaTypes.add("application/vnd.oma.drm.risd+xml");
        ianaTypes.add("application/vnd.oma.group-usage-list+xml");
        ianaTypes.add("application/vnd.oma.lwm2m+json");
        ianaTypes.add("application/vnd.oma.lwm2m+tlv");
        ianaTypes.add("application/vnd.oma.pal+xml");
        ianaTypes.add("application/vnd.oma.poc.detailed-progress-report+xml");
        ianaTypes.add("application/vnd.oma.poc.final-report+xml");
        ianaTypes.add("application/vnd.oma.poc.groups+xml");
        ianaTypes.add("application/vnd.oma.poc.invocation-descriptor+xml");
        ianaTypes.add("application/vnd.oma.poc.optimized-progress-report+xml");
        ianaTypes.add("application/vnd.oma.push");
        ianaTypes.add("application/vnd.oma.scidm.messages+xml");
        ianaTypes.add("application/vnd.oma.xcap-directory+xml");
        ianaTypes.add("application/vnd.omads-email+xml");
        ianaTypes.add("application/vnd.omads-file+xml");
        ianaTypes.add("application/vnd.omads-folder+xml");
        ianaTypes.add("application/vnd.omaloc-supl-init");
        ianaTypes.add("application/vnd.onepager");
        ianaTypes.add("application/vnd.onepagertamp");
        ianaTypes.add("application/vnd.onepagertamx");
        ianaTypes.add("application/vnd.onepagertat");
        ianaTypes.add("application/vnd.onepagertatp");
        ianaTypes.add("application/vnd.onepagertatx");
        ianaTypes.add("application/vnd.openblox.game+xml");
        ianaTypes.add("application/vnd.openblox.game-binary");
        ianaTypes.add("application/vnd.openeye.oeb");
        ianaTypes.add("application/vnd.openstreetmap.data+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.custom-properties+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.customXmlProperties+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.drawing+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.drawingml.chart+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.drawingml.chartshapes+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.drawingml.diagramColors+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.drawingml.diagramData+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.drawingml.diagramLayout+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.drawingml.diagramStyle+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.extended-properties+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.presentationml.commentAuthors+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.presentationml.comments+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.presentationml.handoutMaster+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.presentationml.notesMaster+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.presentationml.notesSlide+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.presentationml.presProps+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.presentationml.presentation");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.presentationml.presentation.main+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.presentationml.slide");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.presentationml.slide+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.presentationml.slideLayout+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.presentationml.slideMaster+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.presentationml.slideUpdateInfo+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.presentationml.slideshow");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.presentationml.slideshow.main+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.presentationml.tableStyles+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.presentationml.tags+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.presentationml.template");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.presentationml.template.main+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.presentationml.viewProps+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.calcChain+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.chartsheet+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.comments+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.connections+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.dialogsheet+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.externalLink+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.pivotCacheDefinition+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.pivotCacheRecords+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.pivotTable+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.queryTable+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.revisionHeaders+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.revisionLog+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.sharedStrings+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheetMetadata+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.table+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.tableSingleCells+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.template");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.template.main+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.userNames+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.volatileDependencies+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.theme+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.themeOverride+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.vmlDrawing");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.wordprocessingml.comments+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.wordprocessingml.document.glossary+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.wordprocessingml.endnotes+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.wordprocessingml.fontTable+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.wordprocessingml.footer+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.wordprocessingml.footnotes+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.wordprocessingml.numbering+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.wordprocessingml.settings+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.wordprocessingml.template");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.wordprocessingml.template.main+xml");
        ianaTypes.add("application/vnd.openxmlformats-officedocument.wordprocessingml.webSettings+xml");
        ianaTypes.add("application/vnd.openxmlformats-package.core-properties+xml");
        ianaTypes.add("application/vnd.openxmlformats-package.digital-signature-xmlsignature+xml");
        ianaTypes.add("application/vnd.openxmlformats-package.relationships+xml");
        ianaTypes.add("application/vnd.oracle.resource+json");
        ianaTypes.add("application/vnd.orange.indata");
        ianaTypes.add("application/vnd.osa.netdeploy");
        ianaTypes.add("application/vnd.osgeo.mapguide.package");
        ianaTypes.add("application/vnd.osgi.bundle");
        ianaTypes.add("application/vnd.osgi.dp");
        ianaTypes.add("application/vnd.osgi.subsystem");
        ianaTypes.add("application/vnd.otps.ct-kip+xml");
        ianaTypes.add("application/vnd.oxli.countgraph");
        ianaTypes.add("application/vnd.pagerduty+json");
        ianaTypes.add("application/vnd.palm");
        ianaTypes.add("application/vnd.panoply");
        ianaTypes.add("application/vnd.paos.xml");
        ianaTypes.add("application/vnd.patentdive");
        ianaTypes.add("application/vnd.patientecommsdoc");
        ianaTypes.add("application/vnd.pawaafile");
        ianaTypes.add("application/vnd.pcos");
        ianaTypes.add("application/vnd.pg.format");
        ianaTypes.add("application/vnd.pg.osasli");
        ianaTypes.add("application/vnd.piaccess.application-licence");
        ianaTypes.add("application/vnd.picsel");
        ianaTypes.add("application/vnd.pmi.widget");
        ianaTypes.add("application/vnd.poc.group-advertisement+xml");
        ianaTypes.add("application/vnd.pocketlearn");
        ianaTypes.add("application/vnd.powerbuilder6");
        ianaTypes.add("application/vnd.powerbuilder6-s");
        ianaTypes.add("application/vnd.powerbuilder7");
        ianaTypes.add("application/vnd.powerbuilder7-s");
        ianaTypes.add("application/vnd.powerbuilder75");
        ianaTypes.add("application/vnd.powerbuilder75-s");
        ianaTypes.add("application/vnd.preminet");
        ianaTypes.add("application/vnd.previewsystems.box");
        ianaTypes.add("application/vnd.proteus.magazine");
        ianaTypes.add("application/vnd.psfs");
        ianaTypes.add("application/vnd.publishare-delta-tree");
        ianaTypes.add("application/vnd.pvi.ptid1");
        ianaTypes.add("application/vnd.pwg-multiplexed");
        ianaTypes.add("application/vnd.pwg-xhtml-print+xml");
        ianaTypes.add("application/vnd.qualcomm.brew-app-res");
        ianaTypes.add("application/vnd.quarantainenet");
        ianaTypes.add("application/vnd.quobject-quoxdocument");
        ianaTypes.add("application/vnd.radisys.moml+xml");
        ianaTypes.add("application/vnd.radisys.msml+xml");
        ianaTypes.add("application/vnd.radisys.msml-audit+xml");
        ianaTypes.add("application/vnd.radisys.msml-audit-conf+xml");
        ianaTypes.add("application/vnd.radisys.msml-audit-conn+xml");
        ianaTypes.add("application/vnd.radisys.msml-audit-dialog+xml");
        ianaTypes.add("application/vnd.radisys.msml-audit-stream+xml");
        ianaTypes.add("application/vnd.radisys.msml-conf+xml");
        ianaTypes.add("application/vnd.radisys.msml-dialog+xml");
        ianaTypes.add("application/vnd.radisys.msml-dialog-base+xml");
        ianaTypes.add("application/vnd.radisys.msml-dialog-fax-detect+xml");
        ianaTypes.add("application/vnd.radisys.msml-dialog-fax-sendrecv+xml");
        ianaTypes.add("application/vnd.radisys.msml-dialog-group+xml");
        ianaTypes.add("application/vnd.radisys.msml-dialog-speech+xml");
        ianaTypes.add("application/vnd.radisys.msml-dialog-transform+xml");
        ianaTypes.add("application/vnd.rainstor.data");
        ianaTypes.add("application/vnd.rapid");
        ianaTypes.add("application/vnd.rar");
        ianaTypes.add("application/vnd.realvnc.bed");
        ianaTypes.add("application/vnd.recordare.musicxml");
        ianaTypes.add("application/vnd.recordare.musicxml+xml");
        ianaTypes.add("application/vnd.restful+json");
        ianaTypes.add("application/vnd.rig.cryptonote");
        ianaTypes.add("application/vnd.route66.link66+xml");
        ianaTypes.add("application/vnd.rs-274x");
        ianaTypes.add("application/vnd.ruckus.download");
        ianaTypes.add("application/vnd.s3sms");
        ianaTypes.add("application/vnd.sailingtracker.track");
        ianaTypes.add("application/vnd.sar");
        ianaTypes.add("application/vnd.sbm.cid");
        ianaTypes.add("application/vnd.sbm.mid2");
        ianaTypes.add("application/vnd.scribus");
        ianaTypes.add("application/vnd.sealed.3df");
        ianaTypes.add("application/vnd.sealed.csf");
        ianaTypes.add("application/vnd.sealed.doc");
        ianaTypes.add("application/vnd.sealed.eml");
        ianaTypes.add("application/vnd.sealed.mht");
        ianaTypes.add("application/vnd.sealed.net");
        ianaTypes.add("application/vnd.sealed.ppt");
        ianaTypes.add("application/vnd.sealed.tiff");
        ianaTypes.add("application/vnd.sealed.xls");
        ianaTypes.add("application/vnd.sealedmedia.softseal.html");
        ianaTypes.add("application/vnd.sealedmedia.softseal.pdf");
        ianaTypes.add("application/vnd.seemail");
        ianaTypes.add("application/vnd.sema");
        ianaTypes.add("application/vnd.semd");
        ianaTypes.add("application/vnd.semf");
        ianaTypes.add("application/vnd.shade-save-file");
        ianaTypes.add("application/vnd.shana.informed.formdata");
        ianaTypes.add("application/vnd.shana.informed.formtemplate");
        ianaTypes.add("application/vnd.shana.informed.interchange");
        ianaTypes.add("application/vnd.shana.informed.package");
        ianaTypes.add("application/vnd.shootproof+json");
        ianaTypes.add("application/vnd.shopkick+json");
        ianaTypes.add("application/vnd.sigrok.session");
        ianaTypes.add("application/vnd.siren+json");
        ianaTypes.add("application/vnd.smaf");
        ianaTypes.add("application/vnd.smart.notebook");
        ianaTypes.add("application/vnd.smart.teacher");
        ianaTypes.add("application/vnd.software602.filler.form+xml");
        ianaTypes.add("application/vnd.software602.filler.form-xml-zip");
        ianaTypes.add("application/vnd.solent.sdkm+xml");
        ianaTypes.add("application/vnd.spotfire.dxp");
        ianaTypes.add("application/vnd.spotfire.sfs");
        ianaTypes.add("application/vnd.sqlite3");
        ianaTypes.add("application/vnd.sss-cod");
        ianaTypes.add("application/vnd.sss-dtf");
        ianaTypes.add("application/vnd.sss-ntf");
        ianaTypes.add("application/vnd.stepmania.package");
        ianaTypes.add("application/vnd.stepmania.stepchart");
        ianaTypes.add("application/vnd.street-stream");
        ianaTypes.add("application/vnd.sun.wadl+xml");
        ianaTypes.add("application/vnd.sus-calendar");
        ianaTypes.add("application/vnd.svd");
        ianaTypes.add("application/vnd.swiftview-ics");
        ianaTypes.add("application/vnd.syncml+xml");
        ianaTypes.add("application/vnd.syncml.dm+wbxml");
        ianaTypes.add("application/vnd.syncml.dm+xml");
        ianaTypes.add("application/vnd.syncml.dm.notification");
        ianaTypes.add("application/vnd.syncml.dmddf+wbxml");
        ianaTypes.add("application/vnd.syncml.dmddf+xml");
        ianaTypes.add("application/vnd.syncml.dmtnds+wbxml");
        ianaTypes.add("application/vnd.syncml.dmtnds+xml");
        ianaTypes.add("application/vnd.syncml.ds.notification");
        ianaTypes.add("application/vnd.tableschema+json");
        ianaTypes.add("application/vnd.tao.intent-module-archive");
        ianaTypes.add("application/vnd.tcpdump.pcap");
        ianaTypes.add("application/vnd.think-cell.ppttc+json");
        ianaTypes.add("application/vnd.tmd.mediaflex.api+xml");
        ianaTypes.add("application/vnd.tml");
        ianaTypes.add("application/vnd.tmobile-livetv");
        ianaTypes.add("application/vnd.tri.onesource");
        ianaTypes.add("application/vnd.trid.tpt");
        ianaTypes.add("application/vnd.triscape.mxs");
        ianaTypes.add("application/vnd.trueapp");
        ianaTypes.add("application/vnd.truedoc");
        ianaTypes.add("application/vnd.ubisoft.webplayer");
        ianaTypes.add("application/vnd.ufdl");
        ianaTypes.add("application/vnd.uiq.theme");
        ianaTypes.add("application/vnd.umajin");
        ianaTypes.add("application/vnd.unity");
        ianaTypes.add("application/vnd.uoml+xml");
        ianaTypes.add("application/vnd.uplanet.alert");
        ianaTypes.add("application/vnd.uplanet.alert-wbxml");
        ianaTypes.add("application/vnd.uplanet.bearer-choice");
        ianaTypes.add("application/vnd.uplanet.bearer-choice-wbxml");
        ianaTypes.add("application/vnd.uplanet.cacheop");
        ianaTypes.add("application/vnd.uplanet.cacheop-wbxml");
        ianaTypes.add("application/vnd.uplanet.channel");
        ianaTypes.add("application/vnd.uplanet.channel-wbxml");
        ianaTypes.add("application/vnd.uplanet.list");
        ianaTypes.add("application/vnd.uplanet.list-wbxml");
        ianaTypes.add("application/vnd.uplanet.listcmd");
        ianaTypes.add("application/vnd.uplanet.listcmd-wbxml");
        ianaTypes.add("application/vnd.uplanet.signal");
        ianaTypes.add("application/vnd.uri-map");
        ianaTypes.add("application/vnd.valve.source.material");
        ianaTypes.add("application/vnd.vcx");
        ianaTypes.add("application/vnd.vd-study");
        ianaTypes.add("application/vnd.vectorworks");
        ianaTypes.add("application/vnd.vel+json");
        ianaTypes.add("application/vnd.verimatrix.vcas");
        ianaTypes.add("application/vnd.veryant.thin");
        ianaTypes.add("application/vnd.ves.encrypted");
        ianaTypes.add("application/vnd.vidsoft.vidconference");
        ianaTypes.add("application/vnd.visio");
        ianaTypes.add("application/vnd.visionary");
        ianaTypes.add("application/vnd.vividence.scriptfile");
        ianaTypes.add("application/vnd.vsf");
        ianaTypes.add("application/vnd.wap.sic");
        ianaTypes.add("application/vnd.wap.slc");
        ianaTypes.add("application/vnd.wap.wbxml");
        ianaTypes.add("application/vnd.wap.wmlc");
        ianaTypes.add("application/vnd.wap.wmlscriptc");
        ianaTypes.add("application/vnd.webturbo");
        ianaTypes.add("application/vnd.wfa.p2p");
        ianaTypes.add("application/vnd.wfa.wsc");
        ianaTypes.add("application/vnd.windows.devicepairing");
        ianaTypes.add("application/vnd.wmc");
        ianaTypes.add("application/vnd.wmf.bootstrap");
        ianaTypes.add("application/vnd.wolfram.mathematica");
        ianaTypes.add("application/vnd.wolfram.mathematica.package");
        ianaTypes.add("application/vnd.wolfram.player");
        ianaTypes.add("application/vnd.wordperfect");
        ianaTypes.add("application/vnd.wqd");
        ianaTypes.add("application/vnd.wrq-hp3000-labelled");
        ianaTypes.add("application/vnd.wt.stf");
        ianaTypes.add("application/vnd.wv.csp+wbxml");
        ianaTypes.add("application/vnd.wv.csp+xml");
        ianaTypes.add("application/vnd.wv.ssp+xml");
        ianaTypes.add("application/vnd.xacml+json");
        ianaTypes.add("application/vnd.xara");
        ianaTypes.add("application/vnd.xfdl");
        ianaTypes.add("application/vnd.xfdl.webform");
        ianaTypes.add("application/vnd.xmi+xml");
        ianaTypes.add("application/vnd.xmpie.cpkg");
        ianaTypes.add("application/vnd.xmpie.dpkg");
        ianaTypes.add("application/vnd.xmpie.plan");
        ianaTypes.add("application/vnd.xmpie.ppkg");
        ianaTypes.add("application/vnd.xmpie.xlim");
        ianaTypes.add("application/vnd.yamaha.hv-dic");
        ianaTypes.add("application/vnd.yamaha.hv-script");
        ianaTypes.add("application/vnd.yamaha.hv-voice");
        ianaTypes.add("application/vnd.yamaha.openscoreformat");
        ianaTypes.add("application/vnd.yamaha.openscoreformat.osfpvg+xml");
        ianaTypes.add("application/vnd.yamaha.remote-setup");
        ianaTypes.add("application/vnd.yamaha.smaf-audio");
        ianaTypes.add("application/vnd.yamaha.smaf-phrase");
        ianaTypes.add("application/vnd.yamaha.through-ngn");
        ianaTypes.add("application/vnd.yamaha.tunnel-udpencap");
        ianaTypes.add("application/vnd.yaoweme");
        ianaTypes.add("application/vnd.yellowriver-custom-menu");
        ianaTypes.add("application/vnd.youtube.yt");
        ianaTypes.add("application/vnd.zul");
        ianaTypes.add("application/vnd.zzazz.deck+xml");
        ianaTypes.add("application/voicexml+xml");
        ianaTypes.add("application/voucher-cms+json");
        ianaTypes.add("application/vq-rtcpxr");
        ianaTypes.add("application/watcherinfo+xml");
        ianaTypes.add("application/webpush-options+json");
        ianaTypes.add("application/whoispp-query");
        ianaTypes.add("application/whoispp-response");
        ianaTypes.add("application/widget");
        ianaTypes.add("application/wita");
        ianaTypes.add("application/wordperfect5.1");
        ianaTypes.add("application/wsdl+xml");
        ianaTypes.add("application/wspolicy+xml");
        ianaTypes.add("application/x-www-form-urlencoded");
        ianaTypes.add("application/x400-bp");
        ianaTypes.add("application/xacml+xml");
        ianaTypes.add("application/xcap-att+xml");
        ianaTypes.add("application/xcap-caps+xml");
        ianaTypes.add("application/xcap-diff+xml");
        ianaTypes.add("application/xcap-el+xml");
        ianaTypes.add("application/xcap-error+xml");
        ianaTypes.add("application/xcap-ns+xml");
        ianaTypes.add("application/xcon-conference-info+xml");
        ianaTypes.add("application/xcon-conference-info-diff+xml");
        ianaTypes.add("application/xenc+xml");
        ianaTypes.add("application/xhtml+xml");
        ianaTypes.add("application/xliff+xml");
        ianaTypes.add("application/xml");
        ianaTypes.add("application/xml-dtd");
        ianaTypes.add("application/xml-external-parsed-entity");
        ianaTypes.add("application/xml-patch+xml");
        ianaTypes.add("application/xmpp+xml");
        ianaTypes.add("application/xop+xml");
        ianaTypes.add("application/xv+xml");
        ianaTypes.add("application/yang");
        ianaTypes.add("application/yang-data+json");
        ianaTypes.add("application/yang-data+xml");
        ianaTypes.add("application/yang-patch+json");
        ianaTypes.add("application/yang-patch+xml");
        ianaTypes.add("application/yin+xml");
        ianaTypes.add("application/zip");
        ianaTypes.add("application/zlib");
        ianaTypes.add("application/zstd");
        ianaTypes.add("image/aces");
        ianaTypes.add("image/avci");
        ianaTypes.add("image/avcs");
        ianaTypes.add("image/bmp");
        ianaTypes.add("image/cgm");
        ianaTypes.add("image/dicom-rle");
        ianaTypes.add("image/emf");
        ianaTypes.add("image/example");
        ianaTypes.add("image/fits");
        ianaTypes.add("image/g3fax");
        ianaTypes.add("image/heic");
        ianaTypes.add("image/heic-sequence");
        ianaTypes.add("image/heif");
        ianaTypes.add("image/heif-sequence");
        ianaTypes.add("image/hej2k");
        ianaTypes.add("image/hsj2");
        ianaTypes.add("image/jls");
        ianaTypes.add("image/jp2");
        ianaTypes.add("image/jph");
        ianaTypes.add("image/jphc");
        ianaTypes.add("image/jpm");
        ianaTypes.add("image/jpx");
        ianaTypes.add("image/jxr");
        ianaTypes.add("image/jxrA");
        ianaTypes.add("image/jxrS");
        ianaTypes.add("image/jxs");
        ianaTypes.add("image/jxsc");
        ianaTypes.add("image/jxsi");
        ianaTypes.add("image/jxss");
        ianaTypes.add("image/naplps");
        ianaTypes.add("image/png");
        ianaTypes.add("image/prs.btif");
        ianaTypes.add("image/prs.pti");
        ianaTypes.add("image/pwg-raster");
        ianaTypes.add("image/t38");
        ianaTypes.add("image/tiff");
        ianaTypes.add("image/tiff-fx");
        ianaTypes.add("image/vnd.adobe.photoshop");
        ianaTypes.add("image/vnd.airzip.accelerator.azv");
        ianaTypes.add("image/vnd.cns.inf2");
        ianaTypes.add("image/vnd.dece.graphic");
        ianaTypes.add("image/vnd.djvu");
        ianaTypes.add("image/vnd.dvb.subtitle");
        ianaTypes.add("image/vnd.dwg");
        ianaTypes.add("image/vnd.dxf");
        ianaTypes.add("image/vnd.fastbidsheet");
        ianaTypes.add("image/vnd.fpx");
        ianaTypes.add("image/vnd.fst");
        ianaTypes.add("image/vnd.fujixerox.edmics-mmr");
        ianaTypes.add("image/vnd.fujixerox.edmics-rlc");
        ianaTypes.add("image/vnd.globalgraphics.pgb");
        ianaTypes.add("image/vnd.microsoft.icon");
        ianaTypes.add("image/vnd.mix");
        ianaTypes.add("image/vnd.mozilla.apng");
        ianaTypes.add("image/vnd.ms-modi");
        ianaTypes.add("image/vnd.net-fpx");
        ianaTypes.add("image/vnd.radiance");
        ianaTypes.add("image/vnd.sealed.png");
        ianaTypes.add("image/vnd.sealedmedia.softseal.gif");
        ianaTypes.add("image/vnd.sealedmedia.softseal.jpg");
        ianaTypes.add("image/vnd.svf");
        ianaTypes.add("image/vnd.tencent.tap");
        ianaTypes.add("image/vnd.valve.source.texture");
        ianaTypes.add("image/vnd.wap.wbmp");
        ianaTypes.add("image/vnd.xiff");
        ianaTypes.add("image/vnd.zbrush.pcx");
        ianaTypes.add("image/wmf");
        ianaTypes.add("text/1d-interleaved-parityfec");
        ianaTypes.add("text/RED");
        ianaTypes.add("text/cache-manifest");
        ianaTypes.add("text/calendar");
        ianaTypes.add("text/css");
        ianaTypes.add("text/csv");
        ianaTypes.add("text/csv-schema");
        ianaTypes.add("text/directory");
        ianaTypes.add("text/dns");
        ianaTypes.add("text/ecmascript");
        ianaTypes.add("text/encaprtp");
        ianaTypes.add("text/example");
        ianaTypes.add("text/flexfec");
        ianaTypes.add("text/fwdred");
        ianaTypes.add("text/grammar-ref-list");
        ianaTypes.add("text/html");
        ianaTypes.add("text/javascript");
        ianaTypes.add("text/jcr-cnd");
        ianaTypes.add("text/markdown");
        ianaTypes.add("text/mizar");
        ianaTypes.add("text/n3");
        ianaTypes.add("text/parameters");
        ianaTypes.add("text/provenance-notation");
        ianaTypes.add("text/prs.fallenstein.rst");
        ianaTypes.add("text/prs.lines.tag");
        ianaTypes.add("text/prs.prop.logic");
        ianaTypes.add("text/raptorfec");
        ianaTypes.add("text/rfc822-headers");
        ianaTypes.add("text/rtf");
        ianaTypes.add("text/rtp-enc-aescm128");
        ianaTypes.add("text/rtploopback");
        ianaTypes.add("text/rtx");
        ianaTypes.add("text/sgml");
        ianaTypes.add("text/strings");
        ianaTypes.add("text/t140");
        ianaTypes.add("text/tab-separated-values");
        ianaTypes.add("text/troff");
        ianaTypes.add("text/turtle");
        ianaTypes.add("text/ulpfec");
        ianaTypes.add("text/uri-list");
        ianaTypes.add("text/vcard");
        ianaTypes.add("text/vnd.DMClientScript");
        ianaTypes.add("text/vnd.IPTC.NITF");
        ianaTypes.add("text/vnd.IPTC.NewsML");
        ianaTypes.add("text/vnd.a");
        ianaTypes.add("text/vnd.abc");
        ianaTypes.add("text/vnd.ascii-art");
        ianaTypes.add("text/vnd.curl");
        ianaTypes.add("text/vnd.debian.copyright");
        ianaTypes.add("text/vnd.dvb.subtitle");
        ianaTypes.add("text/vnd.esmertec.theme-descriptor");
        ianaTypes.add("text/vnd.ficlab.flt");
        ianaTypes.add("text/vnd.fly");
        ianaTypes.add("text/vnd.fmi.flexstor");
        ianaTypes.add("text/vnd.gml");
        ianaTypes.add("text/vnd.graphviz");
        ianaTypes.add("text/vnd.hgl");
        ianaTypes.add("text/vnd.in3d.3dml");
        ianaTypes.add("text/vnd.in3d.spot");
        ianaTypes.add("text/vnd.latex-z");
        ianaTypes.add("text/vnd.motorola.reflex");
        ianaTypes.add("text/vnd.ms-mediapackage");
        ianaTypes.add("text/vnd.net2phone.commcenter.command");
        ianaTypes.add("text/vnd.radisys.msml-basic-layout");
        ianaTypes.add("text/vnd.senx.warpscript");
        ianaTypes.add("text/vnd.si.uricatalogue");
        ianaTypes.add("text/vnd.sosi");
        ianaTypes.add("text/vnd.sun.j2me.app-descriptor");
        ianaTypes.add("text/vnd.trolltech.linguist");
        ianaTypes.add("text/vnd.wap.si");
        ianaTypes.add("text/vnd.wap.sl");
        ianaTypes.add("text/vnd.wap.wml");
        ianaTypes.add("text/vnd.wap.wmlscript");
        ianaTypes.add("text/vtt");
        ianaTypes.add("text/xml");
        ianaTypes.add("text/xml-external-parsed-entity");
        ianaTypes.add("video/1d-interleaved-parityfec");
        ianaTypes.add("video/3gpp");
        ianaTypes.add("video/3gpp-tt");
        ianaTypes.add("video/3gpp2");
        ianaTypes.add("video/BMPEG");
        ianaTypes.add("video/BT656");
        ianaTypes.add("video/CelB");
        ianaTypes.add("video/DV");
        ianaTypes.add("video/H261");
        ianaTypes.add("video/H263");
        ianaTypes.add("video/H263-1998");
        ianaTypes.add("video/H263-2000");
        ianaTypes.add("video/H264");
        ianaTypes.add("video/H264-RCDO");
        ianaTypes.add("video/H264-SVC");
        ianaTypes.add("video/H265");
        ianaTypes.add("video/JPEG");
        ianaTypes.add("video/MP1S");
        ianaTypes.add("video/MP2P");
        ianaTypes.add("video/MP2T");
        ianaTypes.add("video/MP4V-ES");
        ianaTypes.add("video/MPV");
        ianaTypes.add("video/SMPTE292M");
        ianaTypes.add("video/VP8");
        ianaTypes.add("video/encaprtp");
        ianaTypes.add("video/example");
        ianaTypes.add("video/flexfec");
        ianaTypes.add("video/iso.segment");
        ianaTypes.add("video/jpeg2000");
        ianaTypes.add("video/mj2");
        ianaTypes.add("video/mp4");
        ianaTypes.add("video/mpeg4-generic");
        ianaTypes.add("video/nv");
        ianaTypes.add("video/ogg");
        ianaTypes.add("video/pointer");
        ianaTypes.add("video/quicktime");
        ianaTypes.add("video/raptorfec");
        ianaTypes.add("video/rtp-enc-aescm128");
        ianaTypes.add("video/rtploopback");
        ianaTypes.add("video/rtx");
        ianaTypes.add("video/smpte291");
        ianaTypes.add("video/ulpfec");
        ianaTypes.add("video/vc1");
        ianaTypes.add("video/vc2");
        ianaTypes.add("video/vnd.CCTV");
        ianaTypes.add("video/vnd.dece.hd");
        ianaTypes.add("video/vnd.dece.mobile");
        ianaTypes.add("video/vnd.dece.mp4");
        ianaTypes.add("video/vnd.dece.pd");
        ianaTypes.add("video/vnd.dece.sd");
        ianaTypes.add("video/vnd.dece.video");
        ianaTypes.add("video/vnd.directv.mpeg");
        ianaTypes.add("video/vnd.directv.mpeg-tts");
        ianaTypes.add("video/vnd.dlna.mpeg-tts");
        ianaTypes.add("video/vnd.dvb.file");
        ianaTypes.add("video/vnd.fvt");
        ianaTypes.add("video/vnd.hns.video");
        ianaTypes.add("video/vnd.iptvforum.1dparityfec-1010");
        ianaTypes.add("video/vnd.iptvforum.1dparityfec-2005");
        ianaTypes.add("video/vnd.iptvforum.2dparityfec-1010");
        ianaTypes.add("video/vnd.iptvforum.2dparityfec-2005");
        ianaTypes.add("video/vnd.iptvforum.ttsavc");
        ianaTypes.add("video/vnd.iptvforum.ttsmpeg2");
        ianaTypes.add("video/vnd.motorola.video");
        ianaTypes.add("video/vnd.motorola.videop");
        ianaTypes.add("video/vnd.mpegurl");
        ianaTypes.add("video/vnd.ms-playready.media.pyv");
        ianaTypes.add("video/vnd.nokia.interleaved-multimedia");
        ianaTypes.add("video/vnd.nokia.mp4vr");
        ianaTypes.add("video/vnd.nokia.videovoip");
        ianaTypes.add("video/vnd.objectvideo");
        ianaTypes.add("video/vnd.radgamettools.bink");
        ianaTypes.add("video/vnd.radgamettools.smacker");
        ianaTypes.add("video/vnd.sealed.mpeg1");
        ianaTypes.add("video/vnd.sealed.mpeg4");
        ianaTypes.add("video/vnd.sealed.swf");
        ianaTypes.add("video/vnd.sealedmedia.softseal.mov");
        ianaTypes.add("video/vnd.uvvu.mp4");
        ianaTypes.add("video/vnd.vivo");
        ianaTypes.add("video/vnd.youtube.yt");
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    public void testNoSubtype() {
        try {
            MediaType.parse("text");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
    }

    @Test
    public void testInvalidType() {
        try {
            MediaType.parse("te><t/plain");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
    }

    @Test
    public void testInvalidSubtype() {
        try {
            MediaType.parse("text/pl@in");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
    }

    @Test
    public void testWildcardTypeDeclaredSubtype() {
        try {
            MediaType.parse("*/text");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            assertEquals("cannot have a declared subtype with a wildcard type", expected.getMessage());
        }
    }

    @Test
    public void testNonAsciiType() {
        try {
            MediaType.parse("£/plain");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
    }

    @Test
    public void testNonAsciiSubtype() {
        try {
            MediaType.parse("text/£");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
    }

    @Test
    public void testNoType() {
        try {
            MediaType.parse("/plain");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
    }

    @Test
    public void testGetType() {
        assertEquals("text", MediaType.parse("text/plain").type());
        assertEquals("application", MediaType.parse("application/atom+xml; charset=utf-8").type());
    }

    @Test
    public void testGetSubtype() {
        assertEquals("plain", MediaType.parse("text/plain").subtype());
        assertEquals("atom+xml", MediaType.parse("application/atom+xml; charset=utf-8").subtype());
    }

    @Test
    public void testGetParameters() {
        assertEquals(Collections.emptyMap(), MediaType.parse("text/plain").parameters());
        assertEquals(mapOf("one", "1", "2", "two", "three", "3", "charset", "utf-8"), MediaType.parse("application/atom+xml; one=1; 2=two; three=3; charset=utf-8").parameters());
    }

    @Test
    public void testWithParametersInvalidAttribute() {
        try {
            MediaType.parse("text/plain; a=@");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
    }

    @Test
    public void testNonAsciiParameterValue() {
        try {
            MediaType.parse("text/plain; a=a; b=b; c=£");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
    }

    @Test
    public void testNonAsciiParameterName() {
        try {
            MediaType.parse("text/plain; a=a; b=b; £=f");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
    }

    @Test
    public void testParseEmpty() {
        try {
            MediaType.parse("");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
    }

    @Test
    public void testParseRecognizedContentTypes() {
        for (final String str : ianaTypes.stream().map(s -> s.toLowerCase(Locale.US)).collect(Collectors.toSet()))
            try {
                final String[] split = str.split("/");
                final String type = split[0];
                final String subtype = split[1];
                final MediaType mediaType = MediaType.parse(str);

                assertEquals(type, mediaType.type());
                assertEquals(subtype, mediaType.subtype());
                assertNull(mediaType.charset());
                assertEquals(str, mediaType.toString());
            } catch (final IllegalArgumentException e) {
                fail("Unexpected IllegalArgumentException", e);
            }
    }

    @Test
    public void testParseBadContentType() {
        try {
            MediaType.parse("/");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("te<t/plain");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/pl@in");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain;");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; ");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; a");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; a=");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; a=@");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; a=\"@");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; a=1;");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; a=1; ");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; a=1; b");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; a=1; b=");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; a=\u2025");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
    }

    @Test
    public void testGetCharset() {
        assertEquals(null, MediaType.parse("text/plain").charset());
        assertEquals(StandardCharsets.UTF_8, MediaType.parse("text/plain; charset=utf-8").charset());
        assertEquals(StandardCharsets.UTF_16, MediaType.parse("text/plain; charset=utf-16").charset());
    }

    @Test
    public void testIllegalCharsetName() {
        final MediaType type = MediaType.parse("text/plain; charset=\"!@#$%^&*()\"");
        assertNull(type.charset());
    }

    @Test
    public void testUnsupportedCharset() {
        final MediaType type = MediaType.parse("text/plain; charset=utf-64");
        assertNull(type.charset());
    }

    @Test
    public void testToString() {
        final String type = "text/plain; something=\"cr@zy\"; something-else=\"crazy with spaces\"; and-another-thing=\"\"; normal-thing=foo";
        assertEquals(type, MediaType.parse(type).toString());
    }
    
//    @Test
//    public void testQuotedEscaped() {
//        final String s = "abc/efg; param1=\"param\"on e\"";
//        com.google.common.net.MediaType guava = com.google.common.net.MediaType.parse(s);
//        System.out.println("guava:  " + guava);
//        MediaType me = MediaType.parse(s);
//        System.out.println("me:     " + me);
//        HttpMediaType google = new HttpMediaType(s);
//        System.out.println("google: " + google);
//    }

    private static Map<String, String> mapOf(final String... mappings) {
        final Map<String, String> map = new HashMap<>();
        for (int i = 0; i < mappings.length; i += 2)
            map.put(mappings[i], mappings[i + 1]);
        return map;
    }

}
