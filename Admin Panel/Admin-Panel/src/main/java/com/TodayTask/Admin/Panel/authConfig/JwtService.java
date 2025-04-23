package com.TodayTask.Admin.Panel.authConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

	private final String SECRET="TmV3U2VjcmV0S2V5Rm9ySldUU2lnbmluZ1B1cnBvc2VzMTIzNDU2Nzg";

	private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
	
	//generate jwt tocken for username
		public String genearteTocken(String username)
		{
			System.out.println(logger.atInfo());
			System.out.println("tocken generated..");
			// Prepare claims for the token
			Map<String, Object> claims=new HashMap<>();
			return Jwts.builder().claims().add(claims).subject(username).issuedAt(new Date(System.currentTimeMillis()))
	                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)).and().signWith(getSignKey())
	                .compact();
		}
		
		 private SecretKey getSignKey() {System.out.println("get sign key");
	        // Decode the base64 encoded secret key and return a Key object
	        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
	        return Keys.hmacShaKeyFor(keyBytes);
	    }
		 
		 // Extracts all claims from the JWT token.
			public Claims excetrectAll(String tocken)
			{
			System.out.println("extract all.");
		     return Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(tocken).getPayload();
			// return	Jwts.builder().setSigningKey(getSignKey()).build().parseClaimsJws(tocken).getBody();
			}
			
			//extract username
			 public String extractUserName(String token) {
			        // Extract and return the subject claim from the token
			        return excetrectAll(token).getSubject();
			   }
			
			 //extract expiration time of tocken
			public Date extractExpirationTime(String tocken)
			{
				return excetrectAll(tocken).getExpiration();
			}

			//check if the tocken is expired or not
			public boolean isExpired(String tocken)
			{
				return 	extractExpirationTime(tocken).before(new Date());
			}
			 
			//verify tocken
			//Validates the JWT token against the UserDetails
			public boolean verifyTocken(String tocken,UserDetails userDetails)
			{
				//System.out.println("verify tocken called");
				String extractUserName = extractUserName(tocken);
				//System.out.println(extractUserName.equals(userDetails.getUsername()) && !isExpired(tocken));
				return extractUserName.equals(userDetails.getUsername()) && !isExpired(tocken);


			}

}
