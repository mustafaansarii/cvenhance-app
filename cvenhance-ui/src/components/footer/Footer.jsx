
import { Link } from 'react-router-dom';
import BrandLogo from '../shared/BrandLogo';

export default function Footer() {
    const links = {
        product: [
            { name: 'Resume Templates', url: '/templates' },
            { name: 'Resume Checker', url: '/resume-checker' },
            { name: 'My Templates', url: '/my-templates' },
            { name: 'Pricing', url: '/pricing' },
        ],
        company: [
            { name: 'Contact Us', url: '/contact-us' },
            { name: 'Terms & Conditions', url: '/terms' },
            { name: 'Privacy Policy', url: '/privacy-policy' },
            { name: 'Cancellation & Refund', url: '/refund-policy' },
        ],
    };

    return (
        <footer className="border-t border-border bg-card">
            <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">

                {/* Main footer */}
                <div className="grid gap-8 py-8 sm:grid-cols-2 lg:grid-cols-[2fr_1fr_1fr] lg:py-10">

                    {/* Brand */}
                    <div className="max-w-md">
                        <Link
                            to="/"
                            className="inline-flex items-center transition-opacity hover:opacity-80"
                        >
                            <BrandLogo height={32} />
                        </Link>

                        <p className="mt-3 text-sm leading-6 text-muted-foreground">
                            CVEnhance helps job seekers create professional,
                            ATS-friendly resumes with modern templates and
                            practical tools designed to make the job search
                            easier.
                        </p>

                        {/* Social links */}
                        <div className="mt-4 flex items-center gap-2">
                            <a
                                href="https://www.linkedin.com/company/cvenhance/"
                                target="_blank"
                                rel="noopener noreferrer"
                                aria-label="CVEnhance on LinkedIn"
                                className="inline-flex h-8 w-8 items-center justify-center rounded-md text-muted-foreground transition-colors hover:bg-muted hover:text-foreground"
                            >
                                <svg
                                    viewBox="0 0 24 24"
                                    fill="currentColor"
                                    className="h-[18px] w-[18px]"
                                    aria-hidden="true"
                                >
                                    <path d="M20.45 20.45h-3.56v-5.58c0-1.33-.03-3.04-1.85-3.04-1.85 0-2.14 1.45-2.14 2.94v5.68H9.34V8.99h3.42v1.56h.05c.48-.9 1.64-1.85 3.38-1.85 3.61 0 4.28 2.38 4.28 5.47v6.28ZM5.32 7.43a2.07 2.07 0 1 1 0-4.14 2.07 2.07 0 0 1 0 4.14ZM3.54 20.45H7.1V8.99H3.54v11.46Z" />
                                </svg>
                            </a>
                        </div>
                    </div>

                    {/* Product */}
                    <div>
                        <h4 className="text-xs font-semibold uppercase tracking-wider text-foreground">
                            Product
                        </h4>

                        <ul className="mt-3 space-y-2">
                            {links.product.map((link) => (
                                <li key={link.url}>
                                    <Link
                                        to={link.url}
                                        className="text-sm text-muted-foreground transition-colors hover:text-foreground"
                                    >
                                        {link.name}
                                    </Link>
                                </li>
                            ))}
                        </ul>
                    </div>

                    {/* Company */}
                    <div>
                        <h4 className="text-xs font-semibold uppercase tracking-wider text-foreground">
                            Company
                        </h4>

                        <ul className="mt-3 space-y-2">
                            {links.company.map((link) => (
                                <li key={link.url}>
                                    <Link
                                        to={link.url}
                                        className="text-sm text-muted-foreground transition-colors hover:text-foreground"
                                    >
                                        {link.name}
                                    </Link>
                                </li>
                            ))}
                        </ul>
                    </div>
                </div>

                {/* Bottom bar */}
                <div className="flex flex-col gap-2 border-t border-border py-4 text-xs text-muted-foreground sm:flex-row sm:items-center sm:justify-between">
                    <p>
                        © {new Date().getFullYear()} CVEnhance. All rights reserved.
                    </p>

                    <p className="text-muted-foreground/70">
                        Built for better careers.
                    </p>
                </div>
            </div>
        </footer>
    );
}